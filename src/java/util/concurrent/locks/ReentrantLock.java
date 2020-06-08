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

package java.util.concurrent.locks;
import java.util.concurrent.TimeUnit;
import java.util.Collection;

/**
 * 一个拥有基础行为和语义的重入互斥锁，作为使用同步方法和语句访问的隐式监视器锁，并具有扩展功能
 *
 * <p>A {@code ReentrantLock} is <em>owned</em> by the thread last
 * successfully locking, but not yet unlocking it. A thread invoking
 * {@code lock} will return, successfully acquiring the lock, when
 * the lock is not owned by another thread. The method will return
 * immediately if the current thread already owns the lock. This can
 * be checked using methods {@link #isHeldByCurrentThread}, and {@link
 * #getHoldCount}.
 *
 * <p>The constructor for this class accepts an optional
 * <em>fairness</em> parameter.  When set {@code true}, under
 * contention, locks favor granting access to the longest-waiting
 * thread.  Otherwise this lock does not guarantee any particular
 * access order.  Programs using fair locks accessed by many threads
 * may display lower overall throughput (i.e., are slower; often much
 * slower) than those using the default setting, but have smaller
 * variances in times to obtain locks and guarantee lack of
 * starvation. Note however, that fairness of locks does not guarantee
 * fairness of thread scheduling. Thus, one of many threads using a
 * fair lock may obtain it multiple times in succession while other
 * active threads are not progressing and not currently holding the
 * lock.
 * Also note that the untimed {@link #tryLock()} method does not
 * honor the fairness setting. It will succeed if the lock
 * is available even if other threads are waiting.
 *
 * <p>It is recommended practice to <em>always</em> immediately
 * follow a call to {@code lock} with a {@code try} block, most
 * typically in a before/after construction such as:
 *
 *  <pre> {@code
 * class X {
 *   private final ReentrantLock lock = new ReentrantLock();
 *   // ...
 *
 *   public void m() {
 *     lock.lock();  // block until condition holds
 *     try {
 *       // ... method body
 *     } finally {
 *       lock.unlock()
 *     }
 *   }
 * }}</pre>
 *
 * <p>In addition to implementing the {@link Lock} interface, this
 * class defines a number of {@code public} and {@code protected}
 * methods for inspecting the state of the lock.  Some of these
 * methods are only useful for instrumentation and monitoring.
 *
 * <p>Serialization of this class behaves in the same way as built-in
 * locks: a deserialized lock is in the unlocked state, regardless of
 * its state when serialized.
 *
 * <p>This lock supports a maximum of 2147483647 recursive locks by
 * the same thread. Attempts to exceed this limit result in
 * {@link Error} throws from locking methods.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class ReentrantLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = 7373984872572414699L;
    /** 提供所有实现机制的同步器 */
    private final Sync sync;

    /**
     * 此锁的同步控制是基于 AQS 的.
     * 细分为公平和不同平的版本. 使用 AQS 状态来表示持有锁的数量.
     */
    abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -5179523762034025860L;

        /**
         * 执行 Lock#lock 操作.
         * 子类化的主要原因是为了兼容只有不公平的版本的最快途径。
         *
         * Performs {@link Lock#lock}. The main reason for subclassing
         * is to allow fast path for nonfair version.
         */
        abstract void lock();

        /**
         * 执行不公平的方式尝试获取锁.
         * tryAcquire 方法在子类中实现. 但子类和父类同样需要获取不公平锁的方法
         *
         * Performs non-fair tryLock.  tryAcquire is implemented in
         * subclasses, but both need nonfair try for trylock method.
         */
        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) {
                    // overflow
                    throw new Error("Maximum lock count exceeded");
                }
                setState(nextc);
                return true;
            }
            return false;
        }

        /**
         * 由于释放锁是在获取锁之后, 所以释放锁的所有操作都无须考虑线程竞争问题.
         */
        protected final boolean tryRelease(int releases) {
            // 首先把线程重入次数 - 1
            int c = getState() - releases;
            // 如果当前线程不是持有锁的线程，则直接抛出异常.
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            // 如果 state 为 0, 表示锁释放成功.
            if (c == 0) {
                free = true;
                // 把持有锁线程属性字段置为 null.
                setExclusiveOwnerThread(null);
            }
            // 重新设置 state 字段.
            setState(c);
            return free;
        }

        protected final boolean isHeldExclusively() {
            // While we must in general read state before owner,
            // we don't need to do so to check if current thread is owner
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        // Methods relayed from outer class

        final Thread getOwner() {
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }

        final int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }

        final boolean isLocked() {
            return getState() != 0;
        }

        /**
         * Reconstitutes the instance from a stream (that is, deserializes it).
         */
        private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }

    /**
     * Sync object for non-fair locks
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        /**
         * Performs lock.  Try immediate barge, backing up to normal
         * acquire on failure.
         */
        final void lock() {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
            }
            else {
                acquire(1);
            }
        }

        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

    /**
     * Sync object for fair locks
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        final void lock() {
            acquire(1);
        }

        /**
         * 公平地获取锁
         * 其实 tryAcquire 就做了一件事, 就是把锁的状态 state 由 0 变为 1, 或者 + 1 代表重入.
         *
         * 但是这里使用了两个方法来设置 state 字段: CAS 和 setState,
         * 是因为 CAS 之前, 线程还没获得锁, 会出现多个线程竞争.
         * 但是获得锁过后, 只有当前线程才能对该的 state 进行 +1 操作, 所以无需加锁.
         *
         * Fair version of tryAcquire.  Don't grant access unless
         * recursive call or no waiters or is first.
         */
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            // 首先获得当前线程锁的状态
            int c = getState();
            // 如果为 0 , 表示当前锁没有被占用
            if (c == 0) {
                // 因为是公平锁, 需要调用 hasQueuedPredecessors 看看前面是否有前驱节点.
                // 如果没有, 那么就使用 CAS 的方式把锁的状态值置为1.
                if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
                    // 然后把该锁的持有者设置为当前线程
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            // 如果锁的状态不为 0, 并且当前线程就是锁的持有者
            // 那么状态就 + 1, 代表重入一次
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) {
                    throw new Error("Maximum lock count exceeded");
                }
                setState(nextc);
                return true;
            }
            // 如果是其他线程持有了锁, 那么就返回 false.
            return false;
        }
    }

    /**
     * 创建一个非公平的重入锁的实例。
     * 等同于使用 ReentrantLock(false)
     *
     * Creates an instance of {@code ReentrantLock}.
     * This is equivalent to using {@code ReentrantLock(false)}.
     */
    public ReentrantLock() {
        sync = new NonfairSync();
    }

    /**
     * 创建一个带有是否公平的重入锁实例
     * true 为公平，false 为不公平
     *
     */
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }

    /**
     * 获取锁.
     * 如果没被另一个线程持有，则持有并立即返回.
     * 如果当前线程已经持有该锁，那么就增加持有计数器的数量.
     *
     * 如果锁被另一个线程持有，那么当前线程因线程调度而被禁用，并处于休眠状态.
     * 直到获得锁为止，此时持有计数器为1
     */
    public void lock() {
        sync.lock();
    }

    /**
     * 获取锁, 除非当前线程调用 Thread#interrupt 导致线程被中断.
     * 如果该锁被另一个线程持有，那么当前线程因线程调度而被禁用，并处于休眠状态. 直到发生以下两种情况之一：
     *
     * 1、锁被当前线程获取
     * 2、其他线程使用 Thread#interrupt 中断了当前线程.
     *
     * 如果当前线程在进入次方法的时候拥有中断状态，
     * 或者当获取锁的时候被中断, 则抛出 InterruptedException 异常并当把前线程的中断状态清除
     *
     * <p>In this implementation, as this method is an explicit
     * interruption point, preference is given to responding to the
     * interrupt over normal or reentrant acquisition of the lock.
     *
     * @throws InterruptedException if the current thread is interrupted
     */
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    /**
     * 只有在另一个线程未持有锁的时候，才获取锁。
     *
     * 该 "乞求" 行为在某些情况下有用, 即使它打破了公平性.
     * 但如果你想要公平获取锁，就使用 :
     * tryLock(long, TimeUnit)
     * tryLock(0, TimeUnit.SECONDS)
     *
     * @return 只有在锁空闲, 并且当前线程成功获取时候返回 true.
     *         否则返回 false.
     */
    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }

    /**
     * 只有在另一个线程未持有锁的时候，才获取锁。
     *
     * 如果该锁已经被设置为公平排序策略, 如果其他线程正在等待该锁, 那么当前线程不会继续获取该锁.
     * 该方法与 tryLock() 完全相反.
     *
     * 如果你想要一个允许在公平锁上进行讨价还价的定时的 tryLock.
     * 那么就以 timed 和 un-timed 的形式组合在一起:
     *
     * if (lock.tryLock() || lock.tryLock(timeout, unit)) {
     *   ...
     * }
     *
     * 如果锁被另一个线程持有，那么当前线程将被禁用来进行线程调度, 并处于休眠状态.
     * 直到发生以下三件事情之一：
     * 1、锁被当前线程获取
     * 2、其他线程中断了当前线程
     * 3、指定的超时时间已过.
     *
     * @param timeout 等待此锁的时间
     * @param unit    超时时间单位
     * @return 如果获取到返回 true, 否则返回 false
     * @throws InterruptedException 如果当前线程被中断就抛出该异常.
     * @throws NullPointerException 如果时间单位为 null, 抛出该异常.
     */
    public boolean tryLock(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    /**
     * 尝试释放锁
     * 如果当前线程是锁的持有者, 就把锁的持有次数减一.
     * 如果持有次数为0, 该锁就被释放.
     *
     * 如果当前线程不是锁的持有者, 就抛出 IllegalMonitorStateException
     *
     */
    public void unlock() {
        sync.release(1);
    }

    /**
     * 返回一个用于 Lock 实例的 Condition 实例.
     *
     * Condition 实例与内置监视器锁一起使用的时候, 有着与 Object 监视器方法一样的用法
     * (比如: Object#wait(), Object#notify, Object#notifyAll)
     *
     * 如果该锁在 Condition 调用 await() 或者 signal() 的时候，没被持有,
     * 就抛出 IllegalMonitorStateException 异常.
     *
     * 当 Condition 调用 await() , 锁被释放, 并在它们返回之前,
     * 锁会被重新获取, 计数器会恢复到调用 await 方法时的状态.
     *
     * 如果一个线程等待的时候被中断, 那么将会抛出 InterruptedException 异常。
     * 然后清除当前线程的中断位。
     *
     * 等待的线程按先进先出的顺序发出信号.
     * 线程重新获取的数序与最初获取锁的线程的顺序相同，但对于公平锁，等待时间最长的线程优先。
     *
     * @return Condition 对象
     */
    public Condition newCondition() {
        return sync.newCondition();
    }

    /**
     * 查询当前线程持有该锁的数量.
     *
     * class X {
     *   ReentrantLock lock = new ReentrantLock();
     *   public void m() {
     *     assert lock.getHoldCount() == 0;
     *     lock.lock();
     *     try {
     *       ...
     *     } finally {
     *       lock.unlock();
     *     }
     *   }
     * }
     *
     * @return 查询当前线程持有该锁的数量, 如果没有返回0
     *
     */
    public int getHoldCount() {
        return sync.getHoldCount();
    }

    /**
     * 查询当前锁是否被当前线程锁持有.
     *
     * class X {
     *   ReentrantLock lock = new ReentrantLock();
     *
     *   public void m() {
     *       assert lock.isHeldByCurrentThread();
     *       ...
     *   }
     * }
     *
     * 它还可用于确保，以不可重入的方式使用可重入锁
     *
     * class X {
     *   ReentrantLock lock = new ReentrantLock();
     *
     *   public void m() {
     *       assert !lock.isHeldByCurrentThread();
     *       lock.lock();
     *       try {
     *           ...
     *       } finally {
     *           lock.unlock();
     *       }
     *   }
     * }
     *
     * @return 如果当前线程持有该锁，返回 true, 否则 false.
     *
     */
    public boolean isHeldByCurrentThread() {
        return sync.isHeldExclusively();
    }

    /**
     * 查询该锁是否被持有.
     * 该方法是用于监视器系统查询状态使用, 并不是用于同步控制
     */
    public boolean isLocked() {
        return sync.isLocked();
    }

    /**
     * 查询该锁是否是公平锁
     */
    public final boolean isFair() {
        return sync instanceof FairSync;
    }

    /**
     * 返回持有该锁的线程实例.
     * 如果没有线程持有该锁，返回 null
     *
     * 当不是线程持有者调用该线程的时候, 会返回一个当前线程状态的最佳近似值.
     * 比如: 即使有线程试图获取该锁, 但该锁的持有者可能为空.
     * 该方法是用于促进子类的构建，从而提供更广泛的线程状态监视.
     *
     * @return 获取锁的持有者, 如果没有则返回 null
     */
    protected Thread getOwner() {
        return sync.getOwner();
    }

    /**
     * 查询是否有线程正在等待去获取该锁.
     * 请注意因为取消等待随时有可能发谁给你, 所以返回 true 并不保证有线程获得此锁.
     *
     * 该方法主要用于系统状态的监视.
     *
     * @return {@code true} 是否有线程等待去获取该锁.
     */
    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    /**
     * 查询给定的线程是否在排队等待该锁.
     *
     * @param thread 线程
     * @return 如果给定线程排队等待此锁则返回 null
     * @throws NullPointerException 如果线程实例为 null 则抛出
     */
    public final boolean hasQueuedThread(Thread thread) {
        return sync.isQueued(thread);
    }

    /**
     * 返回队列中等待获取锁的大概线程数量.
     *
     * @return 等待获取此锁的大概线程数量
     */
    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire this lock.  Because the actual set of threads may change
     * dynamically while constructing this result, the returned
     * collection is only a best-effort estimate.  The elements of the
     * returned collection are in no particular order.  This method is
     * designed to facilitate construction of subclasses that provide
     * more extensive monitoring facilities.
     *
     * @return the collection of threads
     */
    protected Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

    /**
     * Queries whether any threads are waiting on the given condition
     * associated with this lock. Note that because timeouts and
     * interrupts may occur at any time, a {@code true} return does
     * not guarantee that a future {@code signal} will awaken any
     * threads.  This method is designed primarily for use in
     * monitoring of the system state.
     *
     * @param condition the condition
     * @return {@code true} if there are any waiting threads
     * @throws IllegalMonitorStateException if this lock is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this lock
     * @throws NullPointerException if the condition is null
     */
    public boolean hasWaiters(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    /**
     * Returns an estimate of the number of threads waiting on the
     * given condition associated with this lock. Note that because
     * timeouts and interrupts may occur at any time, the estimate
     * serves only as an upper bound on the actual number of waiters.
     * This method is designed for use in monitoring of the system
     * state, not for synchronization control.
     *
     * @param condition the condition
     * @return the estimated number of waiting threads
     * @throws IllegalMonitorStateException if this lock is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this lock
     * @throws NullPointerException if the condition is null
     */
    public int getWaitQueueLength(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    /**
     * 返回一个集合，其中包含了可能正在等待此锁的线程。
     *
     * 因为实际的线程集合在构造的时候, 可能会动态地改变, 所以返回的集合仅仅是做到最好的结果.
     * 集合的元素没有特定的顺序.
     *
     * 该方法是用于促进子类的构建，从而提供更广泛的线程状态监视.
     *
     * @param condition the condition
     * @return the collection of threads
     * @throws IllegalMonitorStateException 如果该锁没被任何线程持有
     * @throws IllegalArgumentException 如果给定的 condition 没有与该锁有关联
     * @throws NullPointerException 如果 condition 对象为空
     */
    protected Collection<Thread> getWaitingThreads(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    /**
     * Returns a string identifying this lock, as well as its lock state.
     * The state, in brackets, includes either the String {@code "Unlocked"}
     * or the String {@code "Locked by"} followed by the
     * {@linkplain Thread#getName name} of the owning thread.
     *
     * @return a string identifying this lock, as well as its lock state
     */
    public String toString() {
        Thread o = sync.getOwner();
        return super.toString() + ((o == null) ?
                                   "[Unlocked]" :
                                   "[Locked by thread " + o.getName() + "]");
    }
}
