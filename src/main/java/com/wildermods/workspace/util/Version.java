package com.wildermods.workspace.util;

@SuppressWarnings("rawtypes")
public class Version implements Comparable {

	private static final String SPLITTER = "\\.";
	private final String version;
	
	public static final NoVersion NO_VERSION = new NoVersion();
	
	public Version(String version) {
		this.version = version;
	}
	
	@Override
	public boolean equals(Object o) {
		return compareTo(o) == 0;
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof Version || o instanceof CharSequence) {
			if(o instanceof NoVersion) {
				return 1;
			}
			String[] thisVersion = toString().split(SPLITTER);
			String[] otherVersion = o.toString().split(SPLITTER);
			for(int i = 0; i < thisVersion.length && i < otherVersion.length; i++) {
				int compare = thisVersion[i].compareTo(otherVersion[i]);
				if(compare != 0) {
					return compare;
				}
			}
			if(thisVersion.length == otherVersion.length) {
				return 0;
			}
			else if(thisVersion.length > otherVersion.length) {
				return 1;
			}
			else {
				return -1;
			}
		}
		throw new IllegalArgumentException(o.getClass().getCanonicalName());
	}
	
	public String toString() {
		return version;
	}
	
	public static final class NoVersion extends Version {
		
		private NoVersion() {
			super("");
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null || o instanceof NoVersion) {
				return true;
			}
			if(o instanceof CharSequence) {
				return ((CharSequence) o).length() == 0;
			}
			return false;
		}
		
		@Override
		public int compareTo(Object o) {
			if(o instanceof Version || o instanceof CharSequence) {
				return -1;
			}
			throw new IllegalArgumentException(o.getClass().getCanonicalName());
		}
		
		public String toString() {
			return "No version";
		}
		
	}
	
}
