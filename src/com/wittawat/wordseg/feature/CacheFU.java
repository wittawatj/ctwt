
package com.wittawat.wordseg.feature;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;
import weka.core.Attribute;

/**
 * A feature unit which wraps around another feature unit
 * to provide caching. Caching is used on <code>getAttributeValue(int currentIndex)</code>
 *
 * Decorator pattern.
 * 
 * @author Wittawat Jitkrittum
 */
@Deprecated
public class CacheFU extends AbstractFeatureUnit{


    private FeatureUnit featureUnit;
    private Cache cache;

    public CacheFU(String attributeName, FeatureUnit featureUnit) throws CacheException{
        super(attributeName);
        this.featureUnit = featureUnit;
        cache = CacheManager.getInstance().getCache();
        
    }


    public Attribute getAttribute() {
        return featureUnit.getAttribute();

    }

    public double getAttributeValue(int currentIndex) {
        throw new UnsupportedOperationException();
    }

    public Object getRawAttributeValue(int currentIndex) {
        return featureUnit.getRawAttributeValue(currentIndex);
    }



}
