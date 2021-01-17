package com.productengine.test.task.navigator;

import com.productengine.test.task.model.FileObject;

import java.util.function.Consumer;

public interface Navigator {
    /**
     * Finds files from a root directory till the specified depth that
     * contain entered string in their names.
     *
     * @param rootPath path to a root directory.
     *
     * @param depth a depth till which the method will search for files.
     *              Should be only positive number.
     *              If depth = 0 then the method will search for files
     *              in the root directory.
     *
     * @param mask a character or word that files should contain in their names.
     *
     * @param consumer the way files should be shown.
     */
    void getFilesThatMatch(String rootPath, int depth, String mask, Consumer<FileObject> consumer);
}
