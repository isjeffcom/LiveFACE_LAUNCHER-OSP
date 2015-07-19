/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.util

/**
 * Helper class for crating pools of objects. An example use looks like this:
 * <pre>
 * public class MyPooledClass {
 *
 *     private static final SynchronizedPool<MyPooledClass> sPool =
 *             new SynchronizedPool<MyPooledClass>(10);
 *
 *     public static MyPooledClass obtain() {
 *         MyPooledClass instance = sPool.acquire();
 *         return (instance != null) ? instance : new MyPooledClass();
 *     }
 *
 *     public void recycle() {
 *          // Clear state if needed.
 *          sPool.release(this);
 *     }
 *
 *     . . .
 * }
 * </pre>
 *
 * @hide
 */
/**
 * Created by mod on 15-7-18.
 */
open class Pools {
    /**
     * Interface for managing a pool of objects.

     * @param  The pooled type.
     */
    public interface Pool<T> {

        /**
         * @return An instance from the pool if such, null otherwise.
         */
        public fun acquire(): T

        /**
         * Release an instance to the pool.

         * @param instance The instance to release.
         * *
         * @return Whether the instance was put in the pool.
         * *
         * *
         * @throws IllegalStateException If the instance is already in the pool.
         */
        public fun release(instance: T): Boolean
    }

    /* do nothing - hiding constructor */

    private constructor()

    /**
     * Simple (non-synchronized) pool of objects.
     *
     * @param <T> The pooled type.
     */

    /**
     * Simple (non-synchronized) pool of objects.

     * @param  The pooled type.
     *
     * Creates a new instance.

     * @param maxPoolSize The max pool size.
     * *
     * *
     * @throws IllegalArgumentException If the max pool size is less than zero.
     */

    open class SimplePool<T>(maxPoolSize: Int) : Pool<T> {
        private val mPool: Array<Any?>

        private var mPoolSize: Int = 0

        init {
            if (maxPoolSize <= 0) {
                throw IllegalArgumentException("The max pool size must be > 0")
            }
            mPool = arrayOfNulls<Any>(maxPoolSize) //arrayOfNulls<Any>(maxPoolSize)
        }

        SuppressWarnings("unchecked")
        override fun acquire(): T {
            if (mPoolSize > 0) {
                val lastPooledIndex = mPoolSize - 1
                val instance = mPool[lastPooledIndex] as T
                mPool[lastPooledIndex] = null
                mPoolSize--
                return instance
            }
            return null
        }

        override fun release(instance: T): Boolean {
            if (isInPool(instance)) {
                throw IllegalStateException("Already in the pool!")
            }
            if (mPoolSize < mPool.size()) {
                mPool[mPoolSize] = instance
                mPoolSize++
                return true
            }
            return false
        }

        private fun isInPool(instance: T): Boolean {
            for (i in 0..mPoolSize - 1) {
                if (mPool[i] === instance) {
                    return true
                }
            }
            return false
        }
    }

    /**
     * Synchronized) pool of objects.

     * @param  The pooled type.
     */
    /**
     * Creates a new instance.

     * @param maxPoolSize The max pool size.
     * *
     * *
     * @throws IllegalArgumentException If the max pool size is less than zero.
     */
    public class SynchronizedPool<T>(maxPoolSize: Int) : SimplePool<T>(maxPoolSize) {
        private val mLock = Object()

        override fun acquire(): T {
            synchronized (mLock) {
                return super.acquire()
            }
        }

        override fun release(element: T): Boolean {
            synchronized (mLock) {
                return super.release(element)
            }
        }
    }
}