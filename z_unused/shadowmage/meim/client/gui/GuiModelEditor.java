/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.meim.client.gui;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiString;
import shadowmage.ancient_framework.client.gui.elements.IFileSelectCallback;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.client.model.ModelBaseAW;
import shadowmage.ancient_framework.client.model.ModelPiece;
import shadowmage.ancient_framework.client.model.Primitive;
import shadowmage.ancient_framework.client.model.PrimitiveBox;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.ancient_framework.common.utils.Trig;
import shadowmage.meim.client.texture.TextureManager;

public class GuiModelEditor extends GuiContainerAdvanced implements IFileSelectCallback
{

static final int SELECT_LOAD = 0;
static final int SELECT_SAVE = 1;
static final int SELECT_IMPORT_PIECE = 2;
static final int SELECT_LOAD_TEXTURE = 3;
private static final int SELECT_SAVE_TEXTURE = 4;
private static final int SELECT_EXPORT_UV_MAP = 5;

static ModelBaseAW model;

int selectionMode = -1;

int copyNumber = 1;

boolean doSelection;
int selX;
int selZ;

private ModelPiece selectedPiece;
private Primitive selectedPrimitive;

/**
 * values manipulated via mouse input
 */
int lastClickXLeft;
int lastClickZLeft;
int lastClickXRight;
int lastClickZRight;
float yaw;
float pitch;
float viewDistance = 5.f;

/**
 * stored/calc'd values
 */
float viewPosX, viewPosY, viewPosZ, viewTargetX, viewTargetY, viewTargetZ;
private GuiModelEditorSetup setup;

public GuiModelEditor(ContainerBase container)
  {
  super(container);
  this.setup = new GuiModelEditorSetup(this);
  this.shouldCloseOnVanillaKeys = true;
  viewPosZ = 5;
  }

public void initModel()
  {
  model = new ModelBaseAW();
  model.setTextureSize(256, 256);
  ModelPiece piece = new ModelPiece(model, "part1", 0, 0, 0, 0, 0, 0, null);
  PrimitiveBox box = new PrimitiveBox(piece);
  box.setOrigin(0, 0, 0);
  box.setRotation(0, 0, 0);
  box.setBounds(-0.5f, 0.f, -0.5f, 1, 1, 1);
  model.addPiece(piece);
  piece.addPrimitive(box);
  setSelectedPiece(piece);
  setSelectedPrimitive(box);
  }

@Override
public void onElementActivated(IGuiElement element)
  {
 
  }

@Override
public int getXSize()
  {
  return 256;
  }

@Override
public int getYSize()
  {
  return 240;
  }



@Override
public void handleMouseInput()
  {
  super.handleMouseInput();  
  int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
  int z = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
  int button = Mouse.getEventButton();
  int wheelDelta = Mouse.getEventDWheel();
  if(this.isMouseOverControl(x, z))
    {
    return;
    }
  if(button==-1 && wheelDelta!=0)
    {
    if(wheelDelta<0)
      {
      viewDistance+=0.25f;
      }
    else
      {
      viewDistance-=0.25f;
      }
    viewPosX = viewTargetX + viewDistance * MathHelper.sin(yaw) * MathHelper.cos(pitch);
    viewPosZ = viewTargetZ + viewDistance * MathHelper.cos(yaw) * MathHelper.cos(pitch);
    viewPosY = viewTargetY + viewDistance * MathHelper.sin(pitch);
    }
  else if(button==0)
    {
    if(Mouse.getEventButtonState())//left button released
      {
      doSelection = true;
      }
    this.lastClickXLeft = x;
    this.lastClickZLeft = z;     
    }
  else if(Mouse.isButtonDown(0))
    {
    float xInput = x - lastClickXLeft;
    float zInput = z - lastClickZLeft;
    
    float xChange = zInput * MathHelper.sin(pitch) * MathHelper.sin(yaw);
    float zChange = zInput * MathHelper.sin(pitch) * MathHelper.cos(yaw);
    
    xChange += MathHelper.cos(yaw)*xInput;
    zChange -= MathHelper.sin(yaw)*xInput;
    
    float yChange = zInput * MathHelper.cos(pitch);
    
    viewPosX -= xChange * 0.1f;
    viewPosY += yChange * 0.1f;
    viewPosZ -= zChange * 0.1f;
    
    viewTargetX -= xChange * 0.1f;
    viewTargetY += yChange * 0.1f;
    viewTargetZ -= zChange * 0.1f;
    this.lastClickXLeft = x;
    this.lastClickZLeft = z; 
    }
  else if(button==1)
    {
    this.lastClickXRight = x;
    this.lastClickZRight = z;
    } 
  else if(Mouse.isButtonDown(1))
    {
    float yawInput = x - lastClickXRight;
    float pitchInput = z - lastClickZRight;
      
    yaw -= yawInput*Trig.TORADIANS;
    pitch += pitchInput*Trig.TORADIANS;
    if(pitch*Trig.TODEGREES>=89.f)
      {
      pitch = 89.f * Trig.TORADIANS;
      }
    if(pitch*Trig.TODEGREES<=-89.f)
      {
      pitch = -89.f * Trig.TORADIANS;
      }    
    viewPosX = viewTargetX + viewDistance * MathHelper.sin(yaw) * MathHelper.cos(pitch);
    viewPosZ = viewTargetZ + viewDistance * MathHelper.cos(yaw) * MathHelper.cos(pitch);
    viewPosY = viewTargetY + viewDistance * MathHelper.sin(pitch);
    
    this.lastClickXRight = x;
    this.lastClickZRight = z;
    }
  }

@Override
public void renderExtraBackGround(int mouseX, int mouseY, float partialTime)
  {
  GL11.glClearColor(.2f, .2f, .2f, 1.f);
  GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
  setupModelView();
  if(model!=null && doSelection)
    {
    this.doSelection();
    doSelection = false;
    }
  renderGrid();
  enableModelLighting();
  if(model!=null)
    {
    TextureManager.bindTexture();
    model.renderForEditor(getSelectedPiece(), getSelectedPrimitive());//.renderModel();
    TextureManager.resetBoundTexture();
    }  
  resetModelView();
  }

protected void doSelection()
  {
  int posX = Mouse.getX();
  int posY = Mouse.getY();  

  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glClearColor(1.f, 1.f, 1.f, 1.f);
  GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
  GuiModelEditor.model.renderForSelection();   

  byte[] pixelColorsb = new byte[3];
  ByteBuffer pixelColors = ByteBuffer.allocateDirect(3);
  GL11.glReadPixels(posX, posY, 1, 1, GL11.GL_RGB, GL11.GL_BYTE, pixelColors);

  for(int i = 0; i < 3 ; i++)
    {
    pixelColorsb[i] = pixelColors.get(i);
    }
  
  int r = pixelColorsb[0];
  int g = pixelColorsb[1];
  int b = pixelColorsb[2];

  GL11.glEnable(GL11.GL_TEXTURE_2D);
  AWLog.logDebug("colors clicked on: "+r+","+g+","+b);
  int color = (r<<16) | (g<<8) | b;
  AWLog.logDebug("color out: "+color);

  GL11.glClearColor(.2f, .2f, .2f, 1.f);
  GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
  Primitive p = model.getPrimitive(color);
  if(p==null)
    {
    this.setSelectedPiece(null);
    this.setSelectedPrimitive(null);
    }
  else
    {
    this.setSelectedPrimitive(p);
    }
  }

@Override
public void drawExtraForeground(int mouseX, int mouseY, float partialTick)
  {

  }

private void setupModelView()
  {  
  /**
   * load a clean projection matrix
   */
  GL11.glMatrixMode(GL11.GL_PROJECTION);
  GL11.glPushMatrix(); 
  GL11.glLoadIdentity();
  
  /**
   * set up the base projection transformation matrix, as well as view target and position
   * (camera setup)
   */
  float aspect = (float)this.mc.displayWidth/(float)this.mc.displayHeight;  
  GLU.gluPerspective(60.f, aspect, 0.1f, 100.f); 
  GLU.gluLookAt(viewPosX, viewPosY, viewPosZ, viewTargetX, viewTargetY, viewTargetZ, 0, 1, 0);   
    
  /**
   * load a clean model-view matrix
   */
  GL11.glMatrixMode(GL11.GL_MODELVIEW);
  GL11.glPushMatrix();  
  GL11.glLoadIdentity();
  
  /**
   * and finally, clear the depth buffer 
   * (we want to ignore any world/etc, as we're rendering over-top of it all anyway)
   */
  GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
  }

private void resetModelView()
  {
  GL11.glMatrixMode(GL11.GL_PROJECTION);
  GL11.glPopMatrix();
  GL11.glMatrixMode(GL11.GL_MODELVIEW);
  GL11.glPopMatrix();
  }

private void enableModelLighting()
  {
  int bright = this.player.worldObj.getLightBrightnessForSkyBlocks((int)this.player.posX, (int)this.player.posY, (int)this.player.posZ, 0);

  int var11 = bright % 65536;
  int var12 = bright / 65536;
  OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var11 / 1.0F, (float)var12 / 1.0F);

  RenderHelper.enableStandardItemLighting();
  }

int gridDisplayList = -1;

private void renderGrid()
  {
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glLineWidth(2.f);
  if(gridDisplayList>=0)
    {    
    GL11.glCallList(gridDisplayList);
    }
  else
    {
    gridDisplayList = GL11.glGenLists(1);
    GL11.glNewList(gridDisplayList, GL11.GL_COMPILE_AND_EXECUTE);
    GL11.glColor4f(0.f, 0.f, 1.f, 1.f);
    for(int x = -5; x<=5; x++)
      {
      GL11.glBegin(GL11.GL_LINE_LOOP);
      GL11.glVertex3f(x, 0.f, -5.f);
      GL11.glVertex3f(x, 0.f, 5.f);
      GL11.glEnd();    
      }  
    for(int z = -5; z<=5; z++)
      {
      GL11.glBegin(GL11.GL_LINE_LOOP);
      GL11.glVertex3f(-5.f, 0.f, z);
      GL11.glVertex3f(5.f, 0.f, z);
      GL11.glEnd();    
      }
    GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
    GL11.glEndList();
    }
  GL11.glEnable(GL11.GL_LIGHTING);
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

@Override
public void updateScreenContents()
  {  
  if(model==null)
    {
    initModel();
    this.refreshGui();
    }
  }

@Override
public void setupControls()
  {
  pieceLabel = new GuiString(100, this, 160, 12, "None");
  pieceLabel.updateRenderPos(-guiLeft+125, -guiTop);
  this.addElement(pieceLabel);
  primitiveLabel = new GuiString(101, this, 160, 12, "None");
  primitiveLabel.updateRenderPos(-guiLeft+125, -guiTop+14);
  this.addElement(primitiveLabel);
  this.setup.setupControls();//all controls are maintained in the setup class
  
  }

@Override
public void updateControls()
  {
  setup.updateControls(guiLeft, guiTop, width, height);//all controls are maintained in the setup class
  pieceLabel.updateRenderPos(-guiLeft+125, -guiTop);
  primitiveLabel.updateRenderPos(-guiLeft+125, -guiTop+14);
  }

ModelLoader loader = new ModelLoader();

@Override
public void handleFileSelection(File file)
  {
  switch(selectionMode)
  {
  case SELECT_LOAD:
    {
    ModelBaseAW model = loader.loadModel(file);
    GuiModelEditor.model = model;
    setSelectedPiece(null);
    setSelectedPrimitive(null);
    this.setup.updateButtonValues();    
    this.refreshGui();    
    }
    break;
  case SELECT_SAVE:
    { 
    loader.saveModel(model, file);
    }
    break;
  case SELECT_IMPORT_PIECE:
    {
    
    }
    break;
  case SELECT_LOAD_TEXTURE:
    {
    try
      {
      TextureManager.updateTextureContents(ImageIO.read(file));
      } 
    catch (IOException e)
      {
      e.printStackTrace();
      }
    }
    break;
  case SELECT_SAVE_TEXTURE:
    {
    
    }
    break;
  case SELECT_EXPORT_UV_MAP:
    {
    
    }
    break;
  }
  this.refreshGui();
  }

public void copyPiece()
  {
  if(this.selectedPiece!=null)
    {
    ModelPiece newPiece = this.selectedPiece.copy();
    newPiece.setName(selectedPiece.getName()+"CP"+copyNumber);
    copyNumber++;
    if(this.selectedPiece.getParent()==null)
      {
      GuiModelEditor.model.addPiece(newPiece);
      }
    else
      {
      this.selectedPiece.addChild(newPiece);
      }
    this.setSelectedPiece(newPiece);
    this.setSelectedPrimitive(null);
    }
  }

public void deletePiece()
  {
  if(this.selectedPiece!=null)
    {
    GuiModelEditor.model.removePiece(selectedPiece);
    this.setSelectedPiece(null);
    }
  }

Primitive getSelectedPrimitive()
  {
  return selectedPrimitive;
  }

void setSelectedPrimitive(Primitive selectedPrimitive)
  {
  this.setSelection(selectedPrimitive==null? null : selectedPrimitive.parent, selectedPrimitive);
  }

GuiString pieceLabel;
GuiString primitiveLabel;

ModelPiece getSelectedPiece()
  {
  return selectedPiece;
  }

void setSelectedPiece(ModelPiece selectedPiece)
  {
  setSelection(selectedPiece, null);  
  }

private void setSelection(ModelPiece selectedPiece, Primitive selectedPrimitive)
  {
  if(selectedPrimitive!=null && selectedPrimitive.parent!=selectedPiece)
    {
    selectedPiece = selectedPrimitive.parent;
    }
  if(selectedPiece==null)
    {
    selectedPrimitive=null;
    }
  
  this.selectedPiece = selectedPiece;
  pieceLabel.setText(selectedPiece==null? "None" : selectedPiece.getName());
  
  
  this.selectedPrimitive = selectedPrimitive;  
  int num = 1;
  if(selectedPrimitive!=null)
    {
    for(Primitive prim :selectedPrimitive.parent.getPrimitives())
      {
      if(prim==selectedPrimitive)
        {
        break;
        }
      num++;
      }    
    }
  this.primitiveLabel.setText(selectedPrimitive==null ? "None" : "Prim:"+num);
  
  this.setup.addPrimitiveControls();
  this.refreshGui();
  }
}
