/*
 * Copyright (c) 1994, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

/**
 * Class {@code Object} is the root of the class hierarchy.
 * Every class has {@code Object} as a superclass. All objects,
 * including arrays, implement the methods of this class.
 *
 * @author  unascribed
 * @see     java.lang.Class
 * @since   JDK1.0
 */
public class Object {

    private static native void registerNatives(); //
    static {
        registerNatives();
    }

    /**
     * Returns the runtime class of this {@code Object}. The returned
     * {@code Class} object is the object that is locked by {@code
     * static synchronized} methods of the represented class.
     *
     * <p><b>The actual result type is {@code Class<? extends |X|>}
     * where {@code |X|} is the erasure of the static type of the
     * expression on which {@code getClass} is called.</b> For
     * example, no cast is required in this code fragment:</p>
     *
     * <p>
     * {@code Number n = 0;                             }<br>
     * {@code Class<? extends Number> c = n.getClass(); }
     * </p>
     *
     * @return The {@code Class} object that represents the runtime
     *         class of this object.
     * @jls 15.8.2 Class Literals
     */
    public final native Class<?> getClass();

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link java.util.HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     *     method, then calling the {@code hashCode} method on each of
     *     the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link java.lang.Object#equals(java.lang.Object)}
     *     method, then calling the {@code hashCode} method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hash tables.
     * </ul>
     * <p>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java&trade; programming language.)
     *
     * @return  a hash code value for this object.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see     java.lang.System#identityHashCode
     */
    public native int hashCode();

    /**
     * 表示其他对象是否 “等于” 这个对象。
     * 该方法实现了对非空对象引用的等价关系：
     * 1、自反性：对于任意非空引用 x，     x.equals(x) == true.
     * 2、对称性：对于任意非空引用 x, y，  若 x.equals(y) == true，则 y.equals(x) == true.
     * 3、传播性：对于任意非空引用 x, y，  若 x.equals(y) == true, y.equals(z) == true，则 x.equals(z) == true.
     * 4、一贯性：对于任意非空引用 x, y，  多次调用 x.equals(y) 都应一致地返回 true 或 false；如果没有修改比较的信息.
     * 5、对于任意非空引用 x,             x.equals(null) == false
     *
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     *
     * 其他对象应在 Object#equals 上实现最具区分度的等价关系.
     * 也就是说，对于任意非空的引用 x, y, 若 x == y 返回 true, equals 方法返回
     *
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param   obj   the reference object with which to compare.
     * @return  {@code true} if this object is the same as the obj
     *          argument; {@code false} otherwise.
     * @see     #hashCode()
     * @see     java.util.HashMap
     */
    public boolean equals(Object obj) {
        return (this == obj);
    }

    /**
     * 返回一个对象的克隆。“克隆”的精确含义可能取决于对象的类；
     * 通常的意图（作用）是，对于任何的对象 X，
     * 表达式：x.clone() != x；将会返回 true;
     * 表达式 x.clone().getClass() == x.getClass() 将会返回 true
     * 但这些都不是硬性要求。
     *
     * 虽然通常情况，x.clone().equals(x) 将会返回 true，但这些都不是硬性要求。
     *
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression:
     * <blockquote>
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     *
     * 按照惯例，clone() 返回的对象应该包含对父类 clone() 方法的调用。
     * 如果一个类和它的所有父类（除了 Object）都遵守这个惯例，它将会是这种情况：x.clone().getClass() == x.getClass()。
     *
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     *
     * 按照惯例，clone() 返回的对象应该和这个被克隆的对象独立。
     * 为了实现这个独立性，可能需要在 clone() 返回对象之前，修改一个或多个属性。
     * 通常地，这意味着该 clone() 方法，包含克隆对象内部 “深度结构” 的任何可变对象，并用这些副本的引用替换这些对象的引用。
     *
     * 如果一个对象仅包含基本属性、可变对象的引用，这通常会出现：父类的 clone() 没有返回属性供修改的情况。
     *
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     *
     * Object#clone() 执行特定的克隆机制。
     * 首先，如果该类的没有实现接口，将会抛出 CloneNotSupportedException 异常。
     * 注意：所有数组将会被实现 Cloneable 接口，并且返回的数组类型 T[] 是一个泛型。
     *
     * 因此，clone() 创建一个该对象的新的实例，并使用明确的内容初始化对应的字段，字段的内容本身并不克隆。
     * 因此，clone() 对被克隆的对象执行 “浅克隆”，而不是 “深克隆”机制。
     *
     * <p>
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     * <p>
     *
     * Object 对象本身并不实现 Cloneable 接口，所以在 Object 对象上调用 clone 方法时将导致在运行时引发异常
     *
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     *
     * @return     该实例的克隆.
     * @throws  CloneNotSupportedException  若该对象没有实现 {@code Cloneable} 接口.
     *               子类重写该 {@code clone} 方法时，若认为该对象不可被克隆，那么也可以抛出该异常.
     * @see java.lang.Cloneable
     */
    protected native Object clone() throws CloneNotSupportedException;

    /**
     * 返回一个对象的字符串表示。通常 toString 方法返回一个该对象的文本表示。
     * 结果应是一个简洁，且对于人类来说，是具有代表性的。
     * 建议：所有子类都重写此方法。
     *
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     *
     * toString 方法对于 Object 类来说，返回的是一个由 '名称 + @ + 对象哈希码' 组成的字符串。
     * 换一种说法，该方法返回一个和 getClass().getName() + '@' + Integer.toHexString(hashCode()) 值相同的字符串
     *
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  该对象的字符串表现形式.
     */
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * 通知一个在该对象的监视器中，等待的单线程。如果有任意数量的线程在等待这个对象，
     * 只有一个会被唤醒。选择是任意的，并且根据实施自行决定发生。当一个线程调用了该
     * 对象的 wait 方法后，其实就是在等待该对象的监视器。
     *
     * Wakes up a single thread that is waiting on this object's
     * monitor. If any threads are waiting on this object, one of them
     * is chosen to be awakened. The choice is arbitrary and occurs at
     * the discretion of the implementation. A thread waits on an object's
     * monitor by calling one of the {@code wait} methods.
     *
     * 被唤醒的线程无法继续运行，直到当前线程放弃了在该对象的锁。被唤醒的线程将会以
     * 正常的方式，与那些可能在该对象主动竞争的线程进行竞争。例如，被唤醒的线程没有
     * 在下一次竞争锁的时候，享有线程优先级或者劣势。
     *
     * <p>
     * The awakened thread will not be able to proceed until the current
     * thread relinquishes the lock on this object. The awakened thread will
     * compete in the usual manner with any other threads that might be
     * actively competing to synchronize on this object; for example, the
     * awakened thread enjoys no reliable privilege or disadvantage in being
     * the next thread to lock this object.
     *
     * 该方法应只被拥有该对象监视器的线程调用。线程拥有该对象的监视器锁可通过下面其中一个途径：
     * 1、通过执行该对象的同步实例 synchronized 方法
     * 2、通过执行该对象的同步代码块
     * 3、对于 Class 类型的对象，通过执行该对象的静态方法
     *
     * 同一时间，只有一个线程可获得对象的监视器锁。
     *
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. A thread becomes the owner of the
     * object's monitor in one of three ways:
     * <ul>
     * <li>By executing a synchronized instance method of that object.
     * <li>By executing the body of a {@code synchronized} statement
     *     that synchronizes on the object.
     * <li>For objects of type {@code Class,} by executing a
     *     synchronized static method of that class.
     * </ul>
     * <p>
     * Only one thread at a time can own an object's monitor.
     *
     * @throws  IllegalMonitorStateException 若当前线程非监视器锁的持有者，则抛出.
     * @see        java.lang.Object#notifyAll()
     * @see        java.lang.Object#wait()
     */
    public final native void notify();

    /**
     * 通知所有在该对象的监视器锁上等待的线程。线程通过 wait 方法，在该对象的监视器锁上等待。
     *
     * Wakes up all threads that are waiting on this object's monitor. A
     * thread waits on an object's monitor by calling one of the
     * {@code wait} methods.
     *
     * 被唤醒的线程无法继续运行，直到当前线程放弃了在该对象的锁。被唤醒的线程将会以
     * 正常的方式，与那些可能在该对象主动竞争的线程进行竞争。例如，被唤醒的线程没有
     * 在下一次竞争锁的时候，享有线程优先级或者劣势。
     *
     * <p>
     * The awakened threads will not be able to proceed until the current
     * thread relinquishes the lock on this object. The awakened threads
     * will compete in the usual manner with any other threads that might
     * be actively competing to synchronize on this object; for example,
     * the awakened threads enjoy no reliable privilege or disadvantage in
     * being the next thread to lock this object.
     *
     * 该方法应只被拥有该对象监视器锁的线程调用。
     * 详情见 notify 方法描述和获取监视器锁途径。
     *
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @throws  IllegalMonitorStateException  若调用的线程并非对象监视器锁的持有者.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#wait()
     */
    public final native void notifyAll();

    /**
     * 使当前线程等待，直到另一个线程对该对象执行 notify 方法、或者 notifyAll 方法、
     * 又或者到达了指定的时间。
     *
     * Causes the current thread to wait until either another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object, or a
     * specified amount of time has elapsed.
     *
     * <p>
     * 当前线程必须拥有该对象的监视器锁。
     *
     * <p>
     * 该方法使当前线程（称之为 线程 T）置于该对象的 wait-set 中，然后放弃这个对象的所有资源。
     * 线程 T 被禁止线程调度，并且休眠，直到发生以下 4 种情况之一：
     *
     * <ul>
     * <li> 1、其他线程执行该对象的 notify 方法，然后【线程 T】被选中并唤醒。
     * <li> 2、其他线程执行该对象的 notifyAll 方法。
     * <li> 3、其他线程调用 Thread#interrupt 方法，终止了线程 T 的休眠。
     * <li> 4、达到了指定的休眠时间；但若时间为 0，则只能直到 notify 方法被调用。
     * </ul>
     *
     * <p>
     * The current thread must own this object's monitor.
     * <p>
     * This method causes the current thread (call it <var>T</var>) to
     * place itself in the wait set for this object and then to relinquish
     * any and all synchronization claims on this object. Thread <var>T</var>
     * becomes disabled for thread scheduling purposes and lies dormant
     * until one of four things happens:
     * <ul>
     * <li>Some other thread invokes the {@code notify} method for this
     * object and thread <var>T</var> happens to be arbitrarily chosen as
     * the thread to be awakened.
     * <li>Some other thread invokes the {@code notifyAll} method for this
     * object.
     * <li>Some other thread {@linkplain Thread#interrupt() interrupts}
     * thread <var>T</var>.
     * <li>The specified amount of real time has elapsed, more or less.  If
     * {@code timeout} is zero, however, then real time is not taken into
     * consideration and the thread simply waits until notified.
     * </ul>
     *
     * 然后，【线程 T】被移出该对象的 wait-set，并在线程调度方面重新变为可用。然后以正常的
     * 方式与其他线程竞争，以便同步该对象；一旦获得该对象的控制权，所有在该对象上的同步声明
     * 将会被记录为 quo nate - 即逐一调用 wait 方法的情况。然后【线程 T】从 wait 方法
     * 返回过来。因此，在从 wait 方法返回后，该对象和【线程 T】的同步状态完全就像调用 wait
     * 方法时一样。
     *
     * The thread <var>T</var> is then removed from the wait set for this
     * object and re-enabled for thread scheduling. It then competes in the
     * usual manner with other threads for the right to synchronize on the
     * object; once it has gained control of the object, all its
     * synchronization claims on the object are restored to the status quo
     * ante - that is, to the situation as of the time that the {@code wait}
     * method was invoked. Thread <var>T</var> then returns from the
     * invocation of the {@code wait} method. Thus, on return from the
     * {@code wait} method, the synchronization state of the object and of
     * thread {@code T} is exactly as it was when the {@code wait} method
     * was invoked.
     *
     * 线程同样可以不通过 notify、interrupt、超时，进行唤醒，这就是所谓的虚假唤醒。
     * 虽然这个情况在时间当中很少发生，应用程序必须通过测试这个情况来防范，并当条件不满足
     * 时让线程继续等待。换句话说，线程等待必须在循环内体内，就像这样：
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout);
     *         ... // 执行适当的行动
     *     }
     * </pre>
     * (有关此主题的更多信息，见 Doug Lea的Java并发编程 3.2.3 章节
     *  或者 Joshua Bloch 的 Effective Java 第50项)
     *
     * <p>
     * A thread can also wake up without being notified, interrupted, or
     * timing out, a so-called <i>spurious wakeup</i>.  While this will rarely
     * occur in practice, applications must guard against it by testing for
     * the condition that should have caused the thread to be awakened, and
     * continuing to wait if the condition is not satisfied.  In other words,
     * waits should always occur in loops, like this one:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * (For more information on this topic, see Section 3.2.3 in Doug Lea's
     * "Concurrent Programming in Java (Second Edition)" (Addison-Wesley,
     * 2000), or Item 50 in Joshua Bloch's "Effective Java Programming
     * Language Guide" (Addison-Wesley, 2001).
     *
     * <p> 若在当前线程的等待状态，被任何线程调用 Thread#interrupt 来中断，那么将会
     * 抛出 InterruptedException。直到该对象的锁定状态已经恢复到上述的情况。
     *
     * <p>If the current thread is {@linkplain java.lang.Thread#interrupt()
     * interrupted} by any thread before or while it is waiting, then an
     * {@code InterruptedException} is thrown.  This exception is not
     * thrown until the lock status of this object has been restored as
     * described above.
     *
     * <p>
     * 注意 wait 方法，相当于将当前线程放在该对象的 wait-set 方法中，但是，仅解锁此对象。
     * 在线程等待时，在这个线程的其他对象会同步保持锁定状态。
     *
     * <p>
     * Note that the {@code wait} method, as it places the current thread
     * into the wait set for this object, unlocks only this object; any
     * other objects on which the current thread may be synchronized remain
     * locked while the thread waits.
     *
     * <p>
     * 该方法应只被拥有该对象监视器锁的线程调用。
     * 详情见 notify 方法描述和获取监视器锁途径。
     *
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @param      timeout   线程等待的最大毫秒值.
     * @throws  IllegalArgumentException  若毫秒值为负数
     * @throws  IllegalMonitorStateException 若当前线程没有持有该对象的监视器锁
     * @throws  InterruptedException 若任意线程中断了正在等待唤醒的线程，那么该
     *                               异常会被抛出，该线程中断状态将会被清除。
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */
    public final native void wait(long timeout) throws InterruptedException;

    /**
     * 其实这个方法调用的是 wait(long timeout)，而且这个纳秒值是虚假的，原理：timeout++
     *
     * 使当前线程处于等待，直到另一个线程执行了这个对象的 Object#notify 或者 Object#notifyAll，
     * 或者其他线程中断了当前线程，或者超时时间过了。
     *
     * Causes the current thread to wait until another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object, or
     * some other thread interrupts the current thread, or a certain
     * amount of real time has elapsed.
     *
     * <p>
     * 该方法的第一个参数和 wait(long timeout) 方法类似，但是这个方法提供了对时间
     * 更细致的控制。给出以纳秒为单位测量的超时时间：1000000*timeout+nanos
     *
     * <p>
     * This method is similar to the {@code wait} method of one
     * argument, but it allows finer control over the amount of time to
     * wait for a notification before giving up. The amount of real time,
     * measured in nanoseconds, is given by:
     * <blockquote>
     * <pre>
     * 1000000*timeout+nanos</pre></blockquote>
     *
     * 在其他所有方面，该方法和 wait(long timeout) 方法做了一样的事情。特别是，
     * wait(0,0) 和 wait(0) 都代表了同样的意义。
     *
     * <p>
     * In all other respects, this method does the same thing as the
     * method {@link #wait(long)} of one argument. In particular,
     * {@code wait(0, 0)} means the same thing as {@code wait(0)}.
     *
     * <p>
     * 当前线程必须拥有该对象的监视器锁。线程释放该监视器锁，并进行等待，直到
     * 下面两种情况发生其中一种：
     * <ul>
     *     <li>
     *         1、另一个线程通过 notify 方法，或者 notifyAll 方法，唤醒了在该对象的监视器上等待的线程
     *     </li>
     *     <li>
     *         2、超时时间。通过 timeout 去定义，毫秒 + 纳秒的超时时间已过。
     *     </li>
     * </ul>
     *
     * <p>
     * The current thread must own this object's monitor. The thread
     * releases ownership of this monitor and waits until either of the
     * following two conditions has occurred:
     * <ul>
     * <li>Another thread notifies threads waiting on this object's monitor
     *     to wake up either through a call to the {@code notify} method
     *     or the {@code notifyAll} method.
     * <li>The timeout period, specified by {@code timeout}
     *     milliseconds plus {@code nanos} nanoseconds arguments, has
     *     elapsed.
     * </ul>
     *
     * <p>然后线程将会等待，直到它可以重新持有该对象的监视器锁，然后恢复执行。
     * <p>和 wait(long timeout) 一样，可能会发生中断和虚假唤醒的情况，所以
     *    应将等待的代码放到循环体内，避免往下执行。
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout, nanos);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     *
     * <p>
     * 该方法应只被拥有该对象监视器锁的线程调用。
     * 详情见 notify 方法描述和获取监视器锁途径。
     *
     * <p>
     * The thread then waits until it can re-obtain ownership of the
     * monitor and resumes execution.
     * <p>
     * As in the one argument version, interrupts and spurious wakeups are
     * possible, and this method should always be used in a loop:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout, nanos);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @param      timeout   等待时间的毫秒值.
     * @param      nanos     额外的时间，范围 0-999999.
     * @throws  IllegalArgumentException 若 timeout 为负数，
     *                                   或 nanos 值不在 0-999999 范围内
     * @throws  IllegalMonitorStateException  若当前线程没有持有该对象的监视器锁.
     * @throws  InterruptedException 若任意线程中断了正在等待唤醒的线程，那么该
     *                               异常会被抛出，该线程中断状态将会被清除.
     */
    public final void wait(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }

        if (nanos > 0) {
            timeout++;
        }

        wait(timeout);
    }

    /**
     * Causes the current thread to wait until another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object.
     * In other words, this method behaves exactly as if it simply
     * performs the call {@code wait(0)}.
     * <p>
     * The current thread must own this object's monitor. The thread
     * releases ownership of this monitor and waits until another thread
     * notifies threads waiting on this object's monitor to wake up
     * either through a call to the {@code notify} method or the
     * {@code notifyAll} method. The thread then waits until it can
     * re-obtain ownership of the monitor and resumes execution.
     * <p>
     * As in the one argument version, interrupts and spurious wakeups are
     * possible, and this method should always be used in a loop:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait();
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */
    public final void wait() throws InterruptedException {
        wait(0);
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * A subclass overrides the {@code finalize} method to dispose of
     * system resources or to perform other cleanup.
     * <p>
     * The general contract of {@code finalize} is that it is invoked
     * if and when the Java&trade; virtual
     * machine has determined that there is no longer any
     * means by which this object can be accessed by any thread that has
     * not yet died, except as a result of an action taken by the
     * finalization of some other object or class which is ready to be
     * finalized. The {@code finalize} method may take any action, including
     * making this object available again to other threads; the usual purpose
     * of {@code finalize}, however, is to perform cleanup actions before
     * the object is irrevocably discarded. For example, the finalize method
     * for an object that represents an input/output connection might perform
     * explicit I/O transactions to break the connection before the object is
     * permanently discarded.
     * <p>
     * The {@code finalize} method of class {@code Object} performs no
     * special action; it simply returns normally. Subclasses of
     * {@code Object} may override this definition.
     * <p>
     * The Java programming language does not guarantee which thread will
     * invoke the {@code finalize} method for any given object. It is
     * guaranteed, however, that the thread that invokes finalize will not
     * be holding any user-visible synchronization locks when finalize is
     * invoked. If an uncaught exception is thrown by the finalize method,
     * the exception is ignored and finalization of that object terminates.
     * <p>
     * After the {@code finalize} method has been invoked for an object, no
     * further action is taken until the Java virtual machine has again
     * determined that there is no longer any means by which this object can
     * be accessed by any thread that has not yet died, including possible
     * actions by other objects or classes which are ready to be finalized,
     * at which point the object may be discarded.
     * <p>
     * The {@code finalize} method is never invoked more than once by a Java
     * virtual machine for any given object.
     * <p>
     * Any exception thrown by the {@code finalize} method causes
     * the finalization of this object to be halted, but is otherwise
     * ignored.
     *
     * @throws Throwable the {@code Exception} raised by this method
     * @see java.lang.ref.WeakReference
     * @see java.lang.ref.PhantomReference
     * @jls 12.6 Finalization of Class Instances
     */
    protected void finalize() throws Throwable { }
}
