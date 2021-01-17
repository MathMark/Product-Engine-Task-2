package com.productengine.test.task.navigator;

import com.productengine.test.task.model.FileObject;
import sun.rmi.runtime.Log;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ParallelIteratorNavigator implements Navigator {

    private LinkedList<FileObject> list = new LinkedList<>();
    private final Object monitor = new Object();
    private static final Logger logger = Logger.getLogger(ParallelIteratorNavigator.class.getName());

    private class ProducerConsumer {
        private String rootPath;
        private int depth;
        private String mask;
        private Consumer<FileObject> consumer;

        ProducerConsumer(String rootPath, int depth, String mask, Consumer<FileObject> consumer) {
            this.rootPath = rootPath;
            this.depth = depth;
            this.mask = mask;
            this.consumer = consumer;
        }

        private List<FileObject> getDirectories(int depth, File file) {
            String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
            if (directories != null) {
                return Arrays.stream(directories).map(dir -> new FileObject(depth, file.getPath() + "/" + dir)).collect(Collectors.toList());
            }
            return new ArrayList<>();
        }

        private List<FileObject> search(String[] names, int depth, String mask) {
            return Arrays.stream(names).filter(n -> n.contains(mask)).map(n -> new FileObject(depth, n)).collect(Collectors.toList());
        }

        boolean stop = false;

        private void produce() {
            synchronized (monitor) {
                File file = new File(rootPath);
                LinkedList<FileObject> stack = new LinkedList<>();
                int depthCounter;

                stack.push(new FileObject(0, file.getPath()));

                while (!stack.isEmpty()) {
                    FileObject dir = stack.pop();
                    file = new File(dir.getPath());
                    if (file.list() != null) {
                        List<FileObject> fileObjects = search(file.list(), dir.getDepth(), mask);
                        if (fileObjects.size() != 0) {
                            list.addAll(fileObjects);
                            monitor.notify();
                            try {
                                monitor.wait();
                            } catch (InterruptedException e) {
                                logger.severe(e.getMessage());
                            }
                        }
                    }
                    if (dir.getDepth() < depth) {
                        depthCounter = dir.getDepth() + 1;
                        List<FileObject> directories = getDirectories(depthCounter, file);
                        directories.forEach(stack::push);
                    }
                }
                stop = true;
                monitor.notify();
            }
        }

        private void consume() {
            synchronized (monitor) {
                while (!stop) {
                    while (list.size() != 0) {
                        FileObject fileObject = list.pop();
                        consumer.accept(fileObject);
                    }
                    monitor.notify();
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        logger.severe(e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void getFilesThatMatch(String rootPath, int depth, String mask, Consumer<FileObject> fileObjectConsumer) {
        ProducerConsumer producerConsumer = new ProducerConsumer(rootPath, depth, mask, fileObjectConsumer);
        Thread producer = new Thread(producerConsumer::produce);
        Thread consumer = new Thread(producerConsumer::consume);
        producer.start();
        consumer.start();
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            logger.severe(e.getMessage());
        }
    }

}
