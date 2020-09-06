import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinifyStructuresTask extends DefaultTask {
	public MinifyStructuresTask() {
		super();
	}

	@Input
	String templateFolder;

	@TaskAction
	void minify() {
		Path root = new File(templateFolder).toPath();

		if (!root.toFile().exists()) {
			return;
		}

		try (Stream<Path> walk = Files.walk(root)) {
			Iterator<Path> itr = walk.iterator();

			while (itr.hasNext()) {
				Path file = itr.next();
				if (!file.toFile().isFile()) {
					continue;
				}
				String fileName = file.toString();
				String extension = fileName.substring(fileName.length() - 3);
				if (!extension.equals("aws")) {
					continue;
				}

				getLogger().lifecycle("Processing file: {}", file);
				minifyTemplate(file);
			}
		}
		catch (IOException e) {
			getLogger().error("Error iterating filesystem for: {}", root, e);
		}
	}

	private void minifyTemplate(Path file) {
		List<String> result = new ArrayList<>();

		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.ISO_8859_1)) {
			Iterator<String> linesIterator = reader.lines().collect(Collectors.toList()).iterator();

			boolean inLayers = false;
			List<Layer> layers = new ArrayList<>();
			Layer lastLayer = null;
			boolean firstLine = true;
			while (linesIterator.hasNext()) {
				String line = linesIterator.next();
				if (!inLayers) {
					if (firstLine) {
						firstLine = false;
						if (line.contains("minified")) {
							getLogger().warn("File {} is already minified", file);
							return;
						}
						result.add("# minified");
					}
					result.add(line);
					if (line.equals("#### LAYERS ####")) {
						inLayers = true;
					}
				} else {
					if (line.startsWith("layer:")) {
						lastLayer = processLayer(linesIterator, layers, lastLayer, line);
					} else if (line.trim().equals("")) {
						addLayers(layers, result);
						inLayers = false;
					}
				}
			}
		}
		catch (IOException e) {
			getLogger().error("Couldn't read template data {} from {}", file.toString(), file, e);
		}

		writeToTemplate(file, result);
	}

	private void writeToTemplate(Path file, List<String> result) {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.toFile()), StandardCharsets.ISO_8859_1))) {
			for (String line : result) {
				writer.write(line);
				writer.newLine();
			}
		}
		catch (IOException e) {
			getLogger().error("Could not export template to : {} ", file);
		}
	}

	private MinifyStructuresTask.Layer processLayer(Iterator<String> linesIterator, List<Layer> layers, Layer lastLayer, String line) {
		int layerId = Integer.parseInt(line.split(" ")[1]);
		ArrayList<String> layerLines = new ArrayList<>();
		while (linesIterator.hasNext()) {
			line = linesIterator.next();
			if (line.startsWith(":endlayer")) {
				break;
			}
			layerLines.add(line);
		}
		List<Row> rows = getLayerRows(layerLines);
		if (lastLayer != null && lastLayer.isSameLayer(rows)) {
			lastLayer.incrementRepeat();
		} else {
			lastLayer = new Layer(rows, layerId);
			layers.add(lastLayer);
		}
		return lastLayer;
	}

	private List<Row> getLayerRows(ArrayList<String> layerLines) {
		List<Row> rows = new ArrayList<>();
		Row lastRow = null;
		for (String line : layerLines) {
			List<Block> blocks = getRowBlocks(line);
			if (lastRow != null && lastRow.isSameRow(blocks)) {
				lastRow.incrementRepeat();
			} else {
				lastRow = new Row(blocks);
				rows.add(lastRow);
			}
		}
		return rows;
	}

	private List<Block> getRowBlocks(String line) {
		List<Block> blocks = new ArrayList<>();
		Block lastBlock = null;

		for (String id : line.split(",")) {
			int blockId = Integer.parseInt(id);
			if (lastBlock != null && lastBlock.isSameBlock(blockId)) {
				lastBlock.incrementRepeat();
			} else {
				lastBlock = new Block(blockId);
				blocks.add(lastBlock);
			}
		}

		return blocks;
	}

	private void addLayers(List<Layer> layers, List<String> result) {
		for (Layer layer : layers) {
			layer.write(result);
		}
	}

	private static class Layer {
		private final List<Row> rows;
		private final int firstLayerId;
		private int repeatCount = 1;

		private Layer(List<Row> rows, int firstLayerId) {
			this.rows = rows;
			this.firstLayerId = firstLayerId;
		}

		public boolean isSameLayer(List<Row> rows) {
			return this.rows.equals(rows);
		}

		public void incrementRepeat() {
			repeatCount++;
		}

		public void write(List<String> result) {
			result.add("layer: " + (repeatCount == 1 ? String.valueOf(firstLayerId) : firstLayerId + "-" + (firstLayerId + repeatCount - 1)));
			for (Row row : rows) {
				result.add(row.write());
			}
			result.add(":endlayer");
		}
	}

	private static class Row {
		private final List<Block> blocks;
		private int repeatCount = 1;

		public Row(List<Block> blocks) {
			this.blocks = blocks;
		}

		public boolean isSameRow(List<Block> blocks) {
			return this.blocks.equals(blocks);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Row row = (Row) o;
			return repeatCount == row.repeatCount &&
					blocks.equals(row.blocks);
		}

		@Override
		public int hashCode() {
			return Objects.hash(blocks, repeatCount);
		}

		public void incrementRepeat() {
			repeatCount++;
		}

		public String write() {
			return (repeatCount == 1 ? "" : repeatCount + "x") + blocks.stream().map(Block::write).collect(Collectors.joining(","));
		}
	}

	private static class Block {
		private final int id;
		private int repeatCount = 1;

		public Block(int id) {
			this.id = id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Block block = (Block) o;
			return id == block.id &&
					repeatCount == block.repeatCount;
		}

		public boolean isSameBlock(int id) {
			return this.id == id;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, repeatCount);
		}

		public void incrementRepeat() {
			repeatCount++;
		}

		public String write() {
			return repeatCount == 1 ? String.valueOf(id) : id + "|" + repeatCount;
		}
	}
}
