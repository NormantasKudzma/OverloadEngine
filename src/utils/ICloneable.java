package utils;

/**
 * 
 * A workaround for the broken Cloneable interface.
 * 
 * You cannot check if an object in Cloneable, 
 * cast it to Cloneable and invoke clone().
 */
public interface ICloneable extends Cloneable {
	public ICloneable clone();
}
