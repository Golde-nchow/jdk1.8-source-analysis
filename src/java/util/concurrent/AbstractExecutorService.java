/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent;
import java.util.*;

/**
 * Provides default implementations of {@link ExecutorService}
 * execution methods. This class implements the {@code submit},
 * {@code invokeAny} and {@code invokeAll} methods using a
 * {@link RunnableFuture} returned by {@code newTaskFor}, which defaults
 * to the {@link FutureTask} class provided in this package.  For example,
 * the implementation of {@code submit(Runnable)} creates an
 * associated {@code RunnableFuture} that is executed and
 * returned. Subclasses may override the {@code newTaskFor} methods
 * to return {@code RunnableFuture} implementations other than
 * {@code FutureTask}.
 *
 * <p><b>Extension example</b>. Here is a sketch of a class
 * that customizes {@link ThreadPoolExecutor} to use
 * a {@code CustomTask} class instead of the default {@code FutureTask}:
 *  <pre> {@code
 * public class CustomThreadPoolExecutor extends ThreadPoolExecutor {
 *
 *   static class CustomTask<V> implements RunnableFuture<V> {...}
 *
 *   protected <V> RunnableFuture<V> newTaskFor(Callable<V> c) {
 *       return new CustomTask<V>(c);
 *   }
 *   protected <V> RunnableFuture<V> newTaskFor(Runnable r, V v) {
 *       return new CustomTask<V>(r, v);
 *   }
 *   // ... add constructors, etc.
 * }}</pre>
 *
 * @since 1.5
 * @author Doug Lea
 */
public abstract class AbstractExecutorService implements ExecutorService {

    /**
     * 对 Runnable 接口实现类进行封装，
     * 然后返回一个有返回结果的 RunnableFuture 实现类 FutureTask.
     *
     * Returns a {@code RunnableFuture} for the given runnable and default
     * value.
     *
     * @param runnable the runnable task being wrapped
     * @param value the default value for the returned future
     * @param <T> the type of the given value
     * @return a {@code RunnableFuture} which, when run, will run the
     * underlying runnable and which, as a {@code Future}, will yield
     * the given value as its result and provide for cancellation of
     * the underlying task
     * @since 1.6
     */
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }

    /**
     * 对 Callable 接口实现类进行封装，
     * 然后返回一个有返回结果的 RunnableFuture 实现类 FutureTask.
     *
     * Returns a {@code RunnableFuture} for the given callable task.
     *
     * @param callable the callable task being wrapped
     * @param <T> the type of the callable's result
     * @return a {@code RunnableFuture} which, when run, will call the
     * underlying callable and which, as a {@code Future}, will yield
     * the callable's result as its result and provide for
     * cancellation of the underlying task
     * @since 1.6
     */
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask<T>(callable);
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public Future<?> submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);
        return ftask;
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }

    /**
     * invokeAny 的主要实现机制.
     * the main mechanics of invokeAny.
     */
    private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks, boolean timed, long nanos)
        throws InterruptedException, ExecutionException, TimeoutException {
        if (tasks == null) {
            throw new NullPointerException();
        }
        // 首先获取任务的数量
        int ntasks = tasks.size();
        if (ntasks == 0) {
            throw new IllegalArgumentException();
        }
        // 创建一个装执行结果的List实现类.
        ArrayList<Future<T>> futures = new ArrayList<Future<T>>(ntasks);
        // ExecutorCompletionService 不是一个执行器，this才是执行器，而这是执行器的一个包装类.
        // 每个任务结束后，都会将结果保存到内部 BlockingQueue completionQueue 的队列中.
        ExecutorCompletionService<T> ecs = new ExecutorCompletionService<T>(this);

        // For efficiency, especially in executors with limited
        // parallelism, check to see if previously submitted tasks are
        // done before submitting more of them. This interleaving
        // plus the exception mechanics account for messiness of main
        // loop.

        try {
            // Record exceptions so that if we fail to obtain any
            // result, we can throw the last exception we got.
            // 记录异常，以便于我们未能获取任务执行结果的时候，仍然可以抛出我们获取的异常.
            ExecutionException ee = null;
            final long deadline = timed ? System.nanoTime() + nanos : 0L;
            Iterator<? extends Callable<T>> it = tasks.iterator();

            // Start one task for sure; the rest incrementally
            // 开始一个任务，并把结果存在在刚刚创建的 List 集合中.
            futures.add(ecs.submit(it.next()));
            // 任务数量减一.
            --ntasks;
            // 正在执行的任务数, 提交的时候 + 1，结束 - 1
            int active = 1;

            // 使用自旋不断地执行任务，并取出任务结果.
            for (;;) {
                // 由于 BlockingQueue 的 poll 方法不阻塞，随时可以检查.
                Future<T> f = ecs.poll();
                // 如果第一个任务都没执行完
                if (f == null) {
                    // 如果还有任务，那么就执行其他任务，正在执行任务数 + 1
                    if (ntasks > 0) {
                        --ntasks;
                        futures.add(ecs.submit(it.next()));
                        ++active;
                    }
                    // 这里不是表示没有正在执行的任务，而是没有一个任务是成功的.
                    // 因为第一个 if 是用来执行所有的任务的，当不走第一个 if 说明没有任务可以提交了，所有任务都在执行中
                    // 但是如果执行到这里，说明没有任务成功。因为如果成功一个，就会立马返回。
                    else if (active == 0) {
                        break;
                    }
                    // 没有任务需要提交，但是任务还没完成，并且设置了超时时间
                    else if (timed) {
                        // 超时地获取结果
                        f = ecs.poll(nanos, TimeUnit.NANOSECONDS);
                        // 如果还是没有结果，那么抛出超时异常.
                        if (f == null) {
                            throw new TimeoutException();
                        }
                        nanos = deadline - System.nanoTime();
                    }
                    // 没有任务需要提交，但是任务还没完成，并且还没设置超时属性
                    // 使用 take 阻塞获取结果.
                    else {
                        f = ecs.take();
                    }
                }
                // 如果有任务结束了
                if (f != null) {
                    // 那么正在执行的任务 - 1
                    --active;
                    try {
                        // 返回执行的结果
                        // 所以，其实是按顺序地执行任务，但是任务的返回结果是看哪个任务先执行完.
                        // 如果有异常则记录就行了
                        return f.get();
                    } catch (ExecutionException eex) {
                        ee = eex;
                    } catch (RuntimeException rex) {
                        ee = new ExecutionException(rex);
                    }
                }
            }

            // 既然会执行到这里，说明有异常。
            // 如果返回结果的时候，发生异常，那么抛出记录过的异常.
            // 如果没有记录异常，则返回 ExecutionException.
            if (ee == null) {
                ee = new ExecutionException();
            }
            throw ee;

        } finally {
            // 最后把所有执行中的任务尝试取消.
            for (int i = 0, size = futures.size(); i < size; i++) {
                futures.get(i).cancel(true);
            }
        }
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException {
        try {
            return doInvokeAny(tasks, false, 0);
        } catch (TimeoutException cannotHappen) {
            assert false;
            return null;
        }
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        return doInvokeAny(tasks, true, unit.toNanos(timeout));
    }

    // 其实就是循环提交任务.
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if (tasks == null) {
            throw new NullPointerException();
        }
        // 创建一个存储任务结果的 List 集合
        ArrayList<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
        boolean done = false;
        try {
            // 每执行一次循环，就封装一个任务，然后丢到结果集合，然后再执行，执行过程由其他重写了 execute() 的类决定。
            // 为什么还没执行就丢到结果处，因为当调用 get() 的时候，如果没执行完会阻塞.
            for (Callable<T> t : tasks) {
                RunnableFuture<T> f = newTaskFor(t);
                futures.add(f);
                execute(f);
            }
            // 所有任务都提交后，使用循环取出.
            // 如果还没执行完，那么就会阻塞，除非抛出异常。
            // get() 是会抛出 InterruptedException 的，所以这里没捕获中断异常。
            // 所以说，当 get() 抛出中断异常后，就会到达最后的 finally, 取消后面任务的获取操作，并抛出异常.
            for (int i = 0, size = futures.size(); i < size; i++) {
                Future<T> f = futures.get(i);
                if (!f.isDone()) {
                    try {
                        f.get();
                    } catch (CancellationException ignore) {

                    } catch (ExecutionException ignore) {

                    }
                }
            }
            done = true;
            return futures;
        } finally {
            // 如果还有任务没完成，说明在获取结果的线程里，被中断或者其他情况，那么就取消.
            if (!done) {
                for (int i = 0, size = futures.size(); i < size; i++) {
                    futures.get(i).cancel(true);
                }
            }
        }
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        if (tasks == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        ArrayList<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
        boolean done = false;
        try {
            for (Callable<T> t : tasks)
                futures.add(newTaskFor(t));

            final long deadline = System.nanoTime() + nanos;
            final int size = futures.size();

            // Interleave time checks and calls to execute in case
            // executor doesn't have any/much parallelism.
            for (int i = 0; i < size; i++) {
                execute((Runnable)futures.get(i));
                nanos = deadline - System.nanoTime();
                if (nanos <= 0L)
                    return futures;
            }

            for (int i = 0; i < size; i++) {
                Future<T> f = futures.get(i);
                if (!f.isDone()) {
                    if (nanos <= 0L)
                        return futures;
                    try {
                        f.get(nanos, TimeUnit.NANOSECONDS);
                    } catch (CancellationException ignore) {
                    } catch (ExecutionException ignore) {
                    } catch (TimeoutException toe) {
                        return futures;
                    }
                    nanos = deadline - System.nanoTime();
                }
            }
            done = true;
            return futures;
        } finally {
            if (!done)
                for (int i = 0, size = futures.size(); i < size; i++)
                    futures.get(i).cancel(true);
        }
    }

}
