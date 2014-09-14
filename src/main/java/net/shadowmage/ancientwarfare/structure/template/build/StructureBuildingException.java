package net.shadowmage.ancientwarfare.structure.template.build;

public class StructureBuildingException extends Exception
{

/**
 * 
 */
private static final long serialVersionUID = -3374779894692686167L;

protected StructureBuildingException()
  {
  // TODO Auto-generated constructor stub
  }

protected StructureBuildingException(String arg0)
  {
  super(arg0);
  // TODO Auto-generated constructor stub
  }

protected StructureBuildingException(Throwable arg0)
  {
  super(arg0);
  // TODO Auto-generated constructor stub
  }

protected StructureBuildingException(String arg0, Throwable arg1)
  {
  super(arg0, arg1);
  // TODO Auto-generated constructor stub
  }

protected StructureBuildingException(String arg0, Throwable arg1, boolean arg2,
    boolean arg3)
  {
  super(arg0, arg1, arg2, arg3);
  // TODO Auto-generated constructor stub
  }

public static class EntityPlacementException extends StructureBuildingException
{
public EntityPlacementException(String message, Throwable t){super(message, t);}
public EntityPlacementException(String message){super(message);}
}

public static class Block extends StructureBuildingException
{

}

}
