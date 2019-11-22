package object;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import util.Debug;

/**
 * Extension of ConcurrentHashMap so I can add useful methods
 */
public class ExtendedConcurrentHashMap<KeyType, ValueType> extends ConcurrentHashMap<KeyType, ValueType> 
{
	public ExtendedConcurrentHashMap<KeyType, ValueType> factoryCopy()
	{
		ExtendedConcurrentHashMap<KeyType, ValueType> hmCopy = new ExtendedConcurrentHashMap<>();
		
		Iterator<KeyType> it = keySet().iterator();
		for (; it.hasNext();)
		{
			KeyType key = it.next();
			ValueType value = get(key);
			hmCopy.put(key, value);
		}
		
		return hmCopy;
	}
	
	public KeyType getOnlyKey()
	{
		if (size() != 1)
		{
			Debug.stackTrace("Calling getOnlyKey() but size is " + size() + ". HashMap: " + this);
			if (size() == 0)
			{
				return null;
			}
		}
		
		Iterator<KeyType> it = keySet().iterator();
		return it.next();
	}
	
	public void removeAllWithValue(ValueType value)
	{
		Iterator<KeyType> it = keySet().iterator();
		for (; it.hasNext();)
		{
			KeyType key = it.next();
			ValueType valueInHm = get(key);
			if (valueInHm.equals(value))
			{
				remove(key);
			}
		}
	}
}
