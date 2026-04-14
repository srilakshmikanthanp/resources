package com.srilakshmikanthanp.resources.preprocessor;

import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;

public interface ResourcePreprocessor {
	/**
	 * Preprocesses the given resource bundle
	 * and returns the processed bundle.
	 * The original bundle should not be modified.
	 * 
	 * @param bundle The resource bundle to preprocess
	 * @return The preprocessed resource bundle
	 */
	ResourceBundleNode process(ResourceBundleNode bundle);
}
