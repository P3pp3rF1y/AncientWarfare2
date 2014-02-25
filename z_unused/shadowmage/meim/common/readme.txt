MEIM Model Editor In Minecraft





model tree

ModelBase (Entire Model)
    ModelRenderer (BasePiece -- has own location relative to render0,0,0, has own rotation, and offsets)
        Box (Box -- oriented to Piece0,0,0 with own offsets and size, non rotatable)
        ModelRenderer (ChildPiece -- oriented to parentPiece0,0,0, with own rotation and offsets)
          Box (Box of child piece. oriented to childpiece0,0,0, own offsets and size, no rotation)
          ModelRenderer (further child pieces..recursive)
          
          
          
MEIMModelBase
  MEIMModelPart (base parts, list)
    MEIMModelPart (child parts, list)
    MEIMModelBox (list of actual geometry for this part)          