package org.saiku.olap.util;

import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberUtil {
    
    private static final Logger log = LoggerFactory.getLogger(MemberUtil.class);

	public static Level findNativeLevel(Cube nativeCube, String dimension, String hierarchy, String level) {
		
		// Access specified dimension.
		//
		Dimension dim = nativeCube.getDimensions().get(dimension);
		
		Level l = null;
		
		// If the specified dimension has been found, it is possible to proceed.
		// Otherwise a null level is returned by the service.
		//
		if (dim != null) {
			
			log.debug(String.format("Dimension %s found.", dimension));
			
			// Access specified hierarchy.
			//
			Hierarchy h = dim.getHierarchies().get(hierarchy);
			
			// If the hierarchy has not been found, an attempt is made to match against
			// hierarchy unique name or hierarchy name, iterating through all the hierarchies of
			// the selected dimension.
			//
			if (h == null) {
				
				log.debug(String.format("Hierarchy %s not found, looking through iteration.", hierarchy));
				
				for (Hierarchy hlist : dim.getHierarchies()) {
					if (hlist.getUniqueName().equals(hierarchy) || hlist.getName().equals(hierarchy)) {
						h = hlist;
						log.debug(String.format("Hierarchy %s found through iteration.", hierarchy));
						break;
					}
				}
			}

			// The service goes on only if the hierarchy has been identified, otherwise a
			// null level is returned.
			//
			if(h != null) {
				
				log.debug(String.format("Hierarchy %s found.", hierarchy));
				
				l = h.getLevels().get(level);
				
				if (l == null) {
					
					log.debug(String.format("Level %s not found, looking through iteration.", level));

					for (Level lvl : h.getLevels()) {
						if (lvl.getUniqueName().equals(level) || lvl.getName().equals(level)) {
							l = lvl;
							log.debug(String.format("Level %s found through iteration.", level));
							break;
						}
					}
				}
			}
		}
		
		log.debug("Returning level: " + l);
		return l;
	}

	public static Level findNativeLowerLevel(Level upper) {
		
		Hierarchy hierarchy = upper.getHierarchy();
		
		int upperIndex = hierarchy.getLevels().indexOf(upper);
		if(hierarchy.getLevels().size() <= upperIndex + 1) {
			return null;
		}
		
		return hierarchy.getLevels().get(upperIndex + 1);
	}
}
