/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.model;

import hudson.model.Queue.FlyweightTask;

/**
 * {@link Executor} that's temporarily added to carry out tasks that doesn't consume
 * regular executors, like a matrix project parent build.
 *
 * @author Kohsuke Kawaguchi
 * @see FlyweightTask
 */
public class OneOffExecutor extends Executor {
    private Queue.Item item;

    public OneOffExecutor(Computer owner, Queue.Item item) {
        super(owner,-1);
        this.item = item;
    }

    @Override
    protected boolean shouldRun() {
        // TODO: consulting super.shouldRun() here means we'll lose the item if it gets scheduled
        // when super.shouldRun() returns false.
        return super.shouldRun() && item!=null;
    }

    @Override
    protected Queue.Item grabJob() throws InterruptedException {
        Queue.Item r = item;
        item = null;
        return r;
    }

    @Override
    public void run() {
        try {
            super.run();
        } finally {
            owner.remove(this);
        }
    }
}
