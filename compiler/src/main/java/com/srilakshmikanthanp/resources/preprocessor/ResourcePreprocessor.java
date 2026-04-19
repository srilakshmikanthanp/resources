package com.srilakshmikanthanp.resources.preprocessor;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;

public interface ResourcePreprocessor {
	ResourceBundleNode process(Context context, ResourceBundleNode bundle);
}
