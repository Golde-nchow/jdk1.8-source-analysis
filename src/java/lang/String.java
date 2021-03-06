/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.io.ObjectStreamField;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 科普：Unicode 是字符的一个标准的字符编码方案。为每个字符都设置了统一的二进制编码。
 *      通常使用2个字节代表一个字符，所以 Unicode 每个平面都可以组合出 65535 个字符，
 *      一共17个平面。String 可以识别 Unicode 的字符集，包括：UTF-8，UTF-16等。
 *
 *      1、codepoint：
 *         unicode的范围从000000 - 10FFFF，char 的范围只能从 \u0000到\uffff；
 *         所以要用 char 显示剩下的，大于 Ox10000 的部分，只能用两个字符来表示；
 *         那么问题来了，如何识别字符是一个字符组成的还是两个字符组成的？
 *      2、Surrogate：
 *         UTF-16最多编码为 65535，若大于 65535，Unicode的解决方案是从 65535 中
 *         取出 2048个编码，规定他们是 Surrogate，两个一组，重新组合。
 *         · 编号为 U+D800 至 U+DBFF 的规定为「High Surrogates」，共1024个。 '\uD800'与'\uDBFF'之间；
 *         · 编号为 U+DC00 至 U+DFFF 的规定为「Low Surrogates」，共1024个。  '\uDC00'与'\uDFFF'之间；
 *         · 高代理项代码单元 + 低代理项代码单元 = codepoint，可以额外表示 1024*1024个字符。
 *
 * ==============================================================================================
 *
 * String类表示字符的串。Java程序中的字符串文字，例如 “abc”，均是该类的实现。
 *
 * The {@code String} class represents character strings. All
 * string literals in Java programs, such as {@code "abc"}, are
 * implemented as instances of this class.
 *
 * <p>
 * 字符串都是不变的；他们的值在被创建后就不能被改变。字符串缓冲区支持可变字符串。
 * 因为 String 对象都是不可变的，所以他们可以共享。
 * 例如：
 * <blockquote><pre>
 *     String str = "abc";
 * </pre></blockquote><p>
 * 相当于：
 * <blockquote><pre>
 *     char data[] = {'a', 'b', 'c'};
 *     String str = new String(data);
 * </pre></blockquote><p>
 * 以下是一些如何使用字符串的示例：
 * <blockquote><pre>
 *     System.out.println("abc");
 *     String cde = "cde";
 *     System.out.println("abc" + cde);
 *     String c = "abc".substring(2,3);
 *     String d = cde.substring(1, 2);
 * </pre></blockquote>
 *
 * <p>
 * Strings are constant; their values cannot be changed after they
 * are created. String buffers support mutable strings.
 * Because String objects are immutable they can be shared. For example:
 * <blockquote><pre>
 *     String str = "abc";
 * </pre></blockquote><p>
 * is equivalent to:
 * <blockquote><pre>
 *     char data[] = {'a', 'b', 'c'};
 *     String str = new String(data);
 * </pre></blockquote><p>
 * Here are some more examples of how strings can be used:
 * <blockquote><pre>
 *     System.out.println("abc");
 *     String cde = "cde";
 *     System.out.println("abc" + cde);
 *     String c = "abc".substring(2,3);
 *     String d = cde.substring(1, 2);
 * </pre></blockquote>
 *
 * <p>
 * 字符串类包含了：
 * 检查单个字符；比较字符串；查找字符串；提取子串；创建一个字符串的副本，所有的
 * 字符都翻译为大写或小写。案例映射基于字符类指定的 Unicode 标准版本。
 *
 * <p>
 * The class {@code String} includes methods for examining
 * individual characters of the sequence, for comparing strings, for
 * searching strings, for extracting substrings, and for creating a
 * copy of a string with all characters translated to uppercase or to
 * lowercase. Case mapping is based on the Unicode Standard version
 * specified by the {@link java.lang.Character Character} class.
 *
 * <p>
 * Java语言为字符串的拼接操作符 (&nbsp;+&nbsp;) 提供了一个特殊的支持，并将其他
 * 对象转换为字符串。字符串拼接是通过 StringBuilder 或者 StringBuffer 类的
 * append 方法实现的。字符串转换是通过 toString 方法实现的，通过 Object 和
 * 其子类进行定义的。
 * 关于更多的字符串拼接和转换的信息，详细见 Gosling, Joy, and Steele 的
 * <i>Java语言规范</i> (JLS).
 *
 * <p>
 * The Java language provides special support for the string
 * concatenation operator (&nbsp;+&nbsp;), and for conversion of
 * other objects to strings. String concatenation is implemented
 * through the {@code StringBuilder}(or {@code StringBuffer})
 * class and its {@code append} method.
 * String conversions are implemented through the method
 * {@code toString}, defined by {@code Object} and
 * inherited by all classes in Java. For additional information on
 * string concatenation and conversion, see Gosling, Joy, and Steele,
 * <i>The Java Language Specification</i>.
 *
 * <p> 除非另有说明，在这个类的构造器或者方法上，传递一个 null 的参数，将会引发空指针异常。
 *
 * <p> Unless otherwise noted, passing a <tt>null</tt> argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * <p> String 类表示 UTF-16 格式的字符串，其中补充字符由 unicode 代理对表示。
 * (详情请查看 Character类 的<a href="Character.html#unicode">Unicode字符表示</a>)
 *
 * <p>A {@code String} represents a string in the UTF-16 format
 * in which <em>supplementary characters</em> are represented by <em>surrogate
 * pairs</em> (see the section <a href="Character.html#unicode">Unicode
 * Character Representations</a> in the {@code Character} class for
 * more information).
 *
 * 索引值是指字节编码单位，所以一个补充字符在 String 类中占了两个位置。
 *
 * Index values refer to {@code char} code units, so a supplementary
 * character uses two positions in a {@code String}.
 *
 * <p>字符串类提供处理 Unicode 字符的方式，除了那些用于处理 Unicode 字符（即 char 值）
 * 的代码之外。
 *
 * <p>The {@code String} class provides methods for dealing with
 * Unicode code points (i.e., characters), in addition to those for
 * dealing with Unicode code units (i.e., {@code char} values).
 *
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @author  Martin Buchholz
 * @author  Ulf Zibis
 * @see     java.lang.Object#toString()
 * @see     java.lang.StringBuffer
 * @see     java.lang.StringBuilder
 * @see     java.nio.charset.Charset
 * @since   JDK1.0
 */
@SuppressWarnings("all")
public final class String implements java.io.Serializable, Comparable<String>, CharSequence {
    /** 该值用于字符的存储。 */
    private final char value[];

    /** 缓存字符串的哈希编码 */
    private int hash; // 默认为0

    /** 使用JDK 1.0.2的 SerialVersionUID进行互操作性 */
    private static final long serialVersionUID = -6849794470754667710L;

    /**
     * 类字符串是序列化流协议内的特殊套件。
     *
     * 一个字符串实例依据 <a href="{@docRoot}/../platform/serialization/spec/output.html">
     * 对象序列化规则，6.2 章节，"流元素"</a> 被写进 ObjectOutputStream 中。
     *
     * Class String is special cased within the Serialization Stream Protocol.
     *
     * A String instance is written into an ObjectOutputStream according to
     * <a href="{@docRoot}/../platform/serialization/spec/output.html">
     * Object Serialization Specification, Section 6.2, "Stream Elements"</a>
     */
    private static final ObjectStreamField[] serialPersistentFields =
        new ObjectStreamField[0];

    /**
     * 初始化一个新创建的 String 对象，目的是让它代表一个空字符序列。注意该构造函数的使用
     * 是非必须的，因为字符串都是不可变的。
     *
     * Initializes a newly created {@code String} object so that it represents
     * an empty character sequence.  Note that use of this constructor is
     * unnecessary since Strings are immutable.
     */
    public String() {
        this.value = "".value;
    }

    /**
     * 初始化一个新创建的 String 对象，目的是让它代表与参数相同的字符序列；换句话说，
     * 新创建的字符串是目标参数字符串的一个复制。除非明确需要原始副本，否则该构造函数
     * 也是非必须的，因为字符串都是不可变的。
     *
     * Initializes a newly created {@code String} object so that it represents
     * the same sequence of characters as the argument; in other words, the
     * newly created string is a copy of the argument string. Unless an
     * explicit copy of {@code original} is needed, use of this constructor is
     * unnecessary since Strings are immutable.
     *
     * @param  original
     *         A {@code String}
     */
    public String(String original) {
        this.value = original.value;
        this.hash = original.hash;
    }

    /**
     * 分配一个新的 String 对象，目的是可以让它表示：当前包含在字符数组当中的字符序列。
     * 字符数组的内容都是一份复制；后面对字符数组的修改都不会影响到新创建的字符串。
     *
     * Allocates a new {@code String} so that it represents the sequence of
     * characters currently contained in the character array argument. The
     * contents of the character array are copied; subsequent modification of
     * the character array does not affect the newly created string.
     *
     * @param  value
     *         字符串初始值
     */
    public String(char value[]) {
        this.value = Arrays.copyOf(value, value.length);
    }

    /**
     * 原理：使用的是 Arrays.copyOfRange 工具类
     *
     * 分配一个新的，从字符数组截取指定长度的字符，形成的 String对象。
     * offset 参数是数组的开始索引，count 参数定义了截取的长度。截取的内容都是复制的，
     * 后续对 int 数组进行修改，不会影响新创建的字符串
     *
     * Allocates a new {@code String} that contains characters from a subarray
     * of the character array argument. The {@code offset} argument is the
     * index of the first character of the subarray and the {@code count}
     * argument specifies the length of the subarray. The contents of the
     * subarray are copied; subsequent modification of the character array does
     * not affect the newly created string.
     *
     * @param  value
     *         字符的来源
     *
     * @param  offset
     *         初始化偏移量
     *
     * @param  count
     *         截取的长度
     *
     * @throws  IndexOutOfBoundsException
     *          如果偏移量和截取的长度在数组范围之外，则引发该异常
     */
    public String(char value[], int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count <= 0) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            if (offset <= value.length) {
                this.value = "".value;
                return;
            }
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > value.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }
        this.value = Arrays.copyOfRange(value, offset, offset+count);
    }

    /**
     * 分配一个新的，从 Unicode代码点数组 里截取指定长度字符的 String 对象。
     * offset 参数是数组的开始索引，count 参数定义了截取的长度。截取的内容都会被
     * 转换为 char 类型；后续对 int 数组进行修改，不会影响新创建的字符串。
     *
     * Allocates a new {@code String} that contains characters from a subarray
     * of the <a href="Character.html#unicode">Unicode code point</a> array
     * argument.  The {@code offset} argument is the index of the first code
     * point of the subarray and the {@code count} argument specifies the
     * length of the subarray.  The contents of the subarray are converted to
     * {@code char}s; subsequent modification of the {@code int} array does not
     * affect the newly created string.
     *
     * @param  codePoints
     *         Array that is the source of Unicode code points.
     *         数组是Unicode代码点的来源。
     *
     * @param  offset
     *         The initial offset. 初始偏移量。
     *
     * @param  count
     *         The length. 截取的长度。
     *
     * @throws  IllegalArgumentException
     *          如果在 Unicode代码点数组中，发现任何不合法的 Unicode 代码点，则引发异常。
     *
     * @throws  IndexOutOfBoundsException
     *          如果偏移量和截取的长度在数组范围之外，则引发该异常。
     *
     *
     * @since  1.5
     */
    public String(int[] codePoints, int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count <= 0) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            if (offset <= codePoints.length) {
                this.value = "".value;
                return;
            }
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > codePoints.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }

        final int end = offset + count;

        // Pass 1: 校验字符数组的合法性
        int n = count;
        for (int i = offset; i < end; i++) {
            int c = codePoints[i];
            // Bmp 代码点：16位
            if (Character.isBmpCodePoint(c))
                continue;
            else if (Character.isValidCodePoint(c))
                n++;
            else throw new IllegalArgumentException(Integer.toString(c));
        }

        // Pass 2: 分配并填充 char 数组
        final char[] v = new char[n];

        for (int i = offset, j = 0; i < end; i++, j++) {
            int c = codePoints[i];
            // 若为16位字符，则直接转换为 char，因为 char 也是16位
            if (Character.isBmpCodePoint(c))
                v[j] = (char)c;
            else
                // 若为 Surrogate，则在数组占两个位置
                Character.toSurrogates(c, v, j++);
        }

        this.value = v;
    }

    /**
     * 从一个8位整形数组，分配一个新的 String 对象
     *
     * Allocates a new {@code String} constructed from a subarray of an array
     * of 8-bit integer values.
     *
     * <p> offset 参数是截取数组的第一个字节数据的索引，count 参数定义了截取数组的长度。
     *
     * <p> The {@code offset} argument is the index of the first byte of the
     * subarray, and the {@code count} argument specifies the length of the
     * subarray.
     *
     * <p> 截取数组的每个字节都会被转换为一个 char，如上述方法所示。
     *
     * <p> Each {@code byte} in the subarray is converted to a {@code char} as
     * specified in the method above.
     *
     * @deprecated 该方法已过期。此方法未正确将字节转换为字符，截止 JDK1.1，优先通过附带
     * {@link java.nio.charset.Charset}、或者编码名称、或者默认编码的 String 对象的构造器。
     *
     * @param  ascii 转换为 char 类型的字节数据
     *
     * @param  hibyte 每个16位 Unicode 编码单元的高八位。
     *         The top 8 bits of each 16-bit Unicode code unit
     *
     * @param  offset
     *         默认偏移值
     * @param  count
     *         整形数组的数量
     *
     * @throws  IndexOutOfBoundsException
     *          若 {@code offset} 或者 {@code count} 参数不合法。
     *
     * @see  #String(byte[], int)
     * @see  #String(byte[], int, int, java.lang.String)
     * @see  #String(byte[], int, int, java.nio.charset.Charset)
     * @see  #String(byte[], int, int)
     * @see  #String(byte[], java.lang.String)
     * @see  #String(byte[], java.nio.charset.Charset)
     * @see  #String(byte[])
     */
    @Deprecated
    public String(byte ascii[], int hibyte, int offset, int count) {
        checkBounds(ascii, offset, count);
        char value[] = new char[count];

        if (hibyte == 0) {
            for (int i = count; i-- > 0;) {
                value[i] = (char)(ascii[i + offset] & 0xff);
            }
        } else {
            hibyte <<= 8;
            for (int i = count; i-- > 0;) {
                value[i] = (char)(hibyte | (ascii[i + offset] & 0xff));
            }
        }
        this.value = value;
    }

    /**
     * 该方法不进行翻译，因为和上面的方法一致。
     *
     * Allocates a new {@code String} containing characters constructed from
     * an array of 8-bit integer values. Each character <i>c</i>in the
     * resulting string is constructed from the corresponding component
     * <i>b</i> in the byte array such that:
     *
     * <blockquote><pre>
     *     <b><i>c</i></b> == (char)(((hibyte &amp; 0xff) &lt;&lt; 8)
     *                         | (<b><i>b</i></b> &amp; 0xff))
     * </pre></blockquote>
     *
     * @deprecated  This method does not properly convert bytes into
     * characters.  As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@code String} constructors that take a {@link
     * java.nio.charset.Charset}, charset name, or that use the platform's
     * default charset.
     *
     * @param  ascii
     *         The bytes to be converted to characters
     *
     * @param  hibyte
     *         The top 8 bits of each 16-bit Unicode code unit
     *
     * @see  #String(byte[], int, int, java.lang.String)
     * @see  #String(byte[], int, int, java.nio.charset.Charset)
     * @see  #String(byte[], int, int)
     * @see  #String(byte[], java.lang.String)
     * @see  #String(byte[], java.nio.charset.Charset)
     * @see  #String(byte[])
     */
    @Deprecated
    public String(byte ascii[], int hibyte) {
        this(ascii, hibyte, 0, ascii.length);
    }

    /*
     * 核对界限。
     *
     * 用于绑定的常用私有实用程序方法，检查 String(byte[],..) 构造函数
     * 使用的字节数组和请求的偏移和长度值。
     *
     * Common private utility method used to bounds check the byte array
     * and requested offset & length values used by the String(byte[],..)
     * constructors.
     */
    private static void checkBounds(byte[] bytes, int offset, int length) {
        // 长度 >= 0
        if (length < 0)
            throw new StringIndexOutOfBoundsException(length);
        // 偏移量 >= 0
        if (offset < 0)
            throw new StringIndexOutOfBoundsException(offset);
        // 偏移量不能超过指定长度；例：偏移量为3，截取的数组长度不能小于3
        if (offset > bytes.length - length)
            throw new StringIndexOutOfBoundsException(offset + length);
    }

    /**
     * 构造一个新的、通过指定编码来对截取的数组进行解码的字符串。新字符串的长度是 charset
     * 的一个函数，因此可能不等于截取数组的长度。
     *
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the specified charset.  The length of the new {@code String}
     * is a function of the charset, and hence may not be equal to the length
     * of the subarray.
     *
     * 当给出的字节数组无法使用给定的字符集，那么该构造器的行为是未指定的（这个词语有点怪）.
     * 在需要对解码过程更多的控制时，应使用 CharsetDecoder 类。
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the given charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         要解码为字符的字节数组
     *
     * @param  offset
     *         解码第一个字节的索
     *
     * @param  length
     *         解码的字节数

     * @param  charsetName
     *         支持的编码名称
     *
     * @throws  UnsupportedEncodingException
     *          若该编码名称不支持
     *
     * @throws  IndexOutOfBoundsException
     *          若偏移量、长度超过了字节数组的界限
     *
     * @since  JDK1.1
     */
    public String(byte bytes[], int offset, int length, String charsetName)
            throws UnsupportedEncodingException {
        if (charsetName == null)
            throw new NullPointerException("charsetName");
        checkBounds(bytes, offset, length);
        this.value = StringCoding.decode(charsetName, bytes, offset, length);
    }

    /**
     * 构造一个新的、通过指定 Charset 类来对截取的数组进行解码的字符串。新字符串的长度是 charset
     * 的一个函数，因此可能不等于截取数组的长度。
     *
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the specified {@linkplain java.nio.charset.Charset charset}.
     * The length of the new {@code String} is a function of the charset, and
     * hence may not be equal to the length of the subarray.
     *
     * 该方法始终使用 Charset 类的值，来替换畸形输入和未处理的字符序列。
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement string.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         要解码为字符的字节数组
     *
     * @param  offset
     *         解码第一个字节的索
     *
     * @param  length
     *         解码的字节数
     *
     * @param  charset
     *         使用 {@linkplain java.nio.charset.Charset charset} 类来进行解码
     *
     * @throws  IndexOutOfBoundsException
     *          若偏移量、长度超过了字节数组的界限
     *
     * @since  1.6
     */
    public String(byte bytes[], int offset, int length, Charset charset) {
        if (charset == null)
            throw new NullPointerException("charset");
        checkBounds(bytes, offset, length);
        this.value =  StringCoding.decode(charset, bytes, offset, length);
    }

    /**
     * 构造一个新的、通过指定编码名称来对截取的数组进行解码的字符串。新字符串的长度是 charset
     * 的一个函数，因此可能不等于截取数组的长度。
     *
     * Constructs a new {@code String} by decoding the specified array of bytes
     * using the specified {@linkplain java.nio.charset.Charset charset}.  The
     * length of the new {@code String} is a function of the charset, and hence
     * may not be equal to the length of the byte array.
     *
     * 当给出的字节数组无法使用给定的字符集，那么该构造器的行为是未指定的（这个词语有点怪）.
     * 在需要对解码过程更多的控制时，应使用 CharsetDecoder 类。
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the given charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         要解码为字符的字节数组
     *
     * @param  charsetName
     *         支持的编码名称
     *
     * @throws  UnsupportedEncodingException
     *          若该编码名称不支持
     *
     * @since  JDK1.1
     */
    public String(byte bytes[], String charsetName)
            throws UnsupportedEncodingException {
        this(bytes, 0, bytes.length, charsetName);
    }

    /**
     * 构造一个新的、通过指定 Charset 类来对截取的数组进行解码的字符串。新字符串的长度是 charset
     * 的一个函数，因此可能不等于截取数组的长度。
     *
     * Constructs a new {@code String} by decoding the specified array of
     * bytes using the specified {@linkplain java.nio.charset.Charset charset}.
     * The length of the new {@code String} is a function of the charset, and
     * hence may not be equal to the length of the byte array.
     *
     * 该方法始终使用 Charset 类的值，来替换畸形输入和未处理的字符序列。
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement string.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         要解码为字符的字节数组
     *
     * @param  charset
     *         使用 {@linkplain java.nio.charset.Charset charset} 类来进行解码
     *
     * @since  1.6
     */
    public String(byte bytes[], Charset charset) {
        this(bytes, 0, bytes.length, charset);
    }

    /**
     * 构造一个新的、通过平台默认的编码来对截取的数组进行解码的字符串。新字符串的长度是 charset
     * 的一个函数，因此可能不等于截取数组的长度。
     *
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the platform's default charset.  The length of the new
     * {@code String} is a function of the charset, and hence may not be equal
     * to the length of the subarray.
     *
     * 当给出的字节数组无法使用给定的字符集，那么该构造器的行为是未指定的（这个词语有点怪）.
     * 在需要对解码过程更多的控制时，应使用 CharsetDecoder 类。
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         要解码为字符的字节数组
     *
     * @param  offset
     *         偏移量
     *
     * @param  length
     *         解码的字节数
     *
     * @throws  IndexOutOfBoundsException
     *          若偏移量、长度超过了字节数组的界限
     *
     * @since  JDK1.1
     */
    public String(byte bytes[], int offset, int length) {
        checkBounds(bytes, offset, length);
        this.value = StringCoding.decode(bytes, offset, length);
    }

    /**
     * 构造一个新的、通过平台默认的编码来对截取的数组进行解码的字符串。新字符串的长度是 charset
     * 的一个函数，因此可能不等于截取数组的长度。
     *
     * Constructs a new {@code String} by decoding the specified array of bytes
     * using the platform's default charset.  The length of the new {@code
     * String} is a function of the charset, and hence may not be equal to the
     * length of the byte array.
     *
     * 当给出的字节数组无法使用给定的字符集，那么该构造器的行为是未指定的（这个词语有点怪）.
     * 在需要对解码过程更多的控制时，应使用 CharsetDecoder 类。
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         要解码为字符的字节数组
     *
     * @since  JDK1.1
     */
    public String(byte bytes[]) {
        this(bytes, 0, bytes.length);
    }

    /**
     * 分配一个新的、由字符串缓冲区构造出来的字符串。字符串缓冲区的内容都是复制的，
     * 对缓冲区的修改不会影响新创建的字符串。
     *
     * Allocates a new string that contains the sequence of characters
     * currently contained in the string buffer argument. The contents of the
     * string buffer are copied; subsequent modification of the string buffer
     * does not affect the newly created string.
     *
     * @param  buffer
     *         {@code StringBuffer}类
     */
    public String(StringBuffer buffer) {
        synchronized(buffer) {
            this.value = Arrays.copyOf(buffer.getValue(), buffer.length());
        }
    }

    /**
     * 分配一个新的、由 StringBuffer 构造出来的字符串。字符串缓冲区的内容都是复制的，
     * 对缓冲区的修改不会影响新创建的字符串。
     *
     * Allocates a new string that contains the sequence of characters
     * currently contained in the string builder argument. The contents of the
     * string builder are copied; subsequent modification of the string builder
     * does not affect the newly created string.
     *
     * 提供此构造器来简易地迁移到 StringBuilder。通过 toString 方法来获取 StringBuilder
     * 内的字符串可能会更快，并且通常是优先选择的。
     *
     * <p> This constructor is provided to ease migration to {@code
     * StringBuilder}. Obtaining a string from a string builder via the {@code
     * toString} method is likely to run faster and is generally preferred.
     *
     * @param   builder
     *          {@code StringBuilder}类
     *
     * @since  1.5
     */
    public String(StringBuilder builder) {
        this.value = Arrays.copyOf(builder.getValue(), builder.length());
    }

    /*
    * 包私有构造器，用于迅速地分享数组值。该构造器的 share 值始终为 true。
    * 需要一个单独的构造函数，因为我们已经拥有一个公共的 String(char[]) 构造函数，
    * 用于生成给定字符数组的副本。
    *
    * Package private constructor which shares value array for speed.
    * this constructor is always expected to be called with share==true.
    * a separate constructor is needed because we already have a public
    * String(char[]) constructor that makes a copy of the given char[].
    */
    String(char[] value, boolean share) {
        // 对 share 值进行断言 : "不支持 shared == false";
        this.value = value;
    }

    /**
     * 返回字符串的长度。
     * 该长度和字符串内的 <a href="Character.html#unicode">Unicode代码点</a> 的数量一致。
     *
     * Returns the length of this string.
     * The length is equal to the number of <a href="Character.html#unicode">Unicode
     * code units</a> in the string.
     *
     * @return  该对象表示的字符序列的长度。
     */
    public int length() {
        return value.length;
    }

    /**
     * 如果 length() 为 0，返回 true，否则返回 false
     *
     * @since 1.6
     */
    public boolean isEmpty() {
        return value.length == 0;
    }

    /**
     * 在指定的索引位置返回对应的字符。索引的范围为 0 至 length()-1。
     * 第一个字符是在字符序列索引为 0 的位置，下一个字符就在索引 1 位，以此类推。
     *
     * Returns the {@code char} value at the
     * specified index. An index ranges from {@code 0} to
     * {@code length() - 1}. The first {@code char} value of the sequence
     * is at index {@code 0}, the next at index {@code 1},
     * and so on, as for array indexing.
     *
     * 若字符值对应的这个索引是一个 surrogate，那么该 surrogate 的值都会被返回。
     *
     * <p>If the {@code char} value specified by the index is a
     * <a href="Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     *
     * @param      index   索引下标
     * @return     指定下标对应的字符。
     * @exception  IndexOutOfBoundsException  若索引值不在字符串范围内
     */
    public char charAt(int index) {
        if ((index < 0) || (index >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[index];
    }

    /**
     * 返回指定索引的字符（以 Unicode代码点 的形式）。
     * 该索引引用 char 的值（Unicode代码单元），范围为 0 至 length()-1.
     *
     * Returns the character (Unicode code point) at the specified
     * index. The index refers to {@code char} values
     * (Unicode code units) and ranges from {@code 0} to
     * {@link #length()}{@code  - 1}.
     *
     * 若 char 值给出的索引是处于高代理位，并且后面的索引小于此字符串的长度，
     * 并且后面索引的 char 值是处于低代理位范围内，则返回与此代码点对应的
     * 补充代码点。否则，只有对应索引的 char 值被返回。
     *
     * <p> If the {@code char} value specified at the given index
     * is in the high-surrogate range, the following index is less
     * than the length of this {@code String}, and the
     * {@code char} value at the following index is in the
     * low-surrogate range, then the supplementary code point
     * corresponding to this surrogate pair is returned. Otherwise,
     * the {@code char} value at the given index is returned.
     *
     * @param      index 索引值
     * @return     对应索引的 char 值的代码点
     * @exception  IndexOutOfBoundsException 若索引值不在字符串的范围内
     * @since      1.5
     */
    public int codePointAt(int index) {
        if ((index < 0) || (index >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointAtImpl(value, index, value.length);
    }

    /**
     * 返回指定索引的前一个索引的 char 值代码点。
     * 该索引引用 char 的值（Unicode代码单元），范围为 0 至 CharSequence#length()。
     *
     * Returns the character (Unicode code point) before the specified
     * index. The index refers to {@code char} values
     * (Unicode code units) and ranges from {@code 1} to {@link
     * CharSequence#length() length}.
     *
     * 若 char 值在（index - 1）中是低代理位范围，且在（index - 2）不是负数，
     * 且 (index - 2) 的值是高代理位，那么这一对 surrogate 代码点值就会被返回。
     * 若 char 值在（index - 1）是一对 surrogate 中的高代理位或低代理位，
     * 那么这个单独的 surrogate 值会被返回。
     *
     * <p> If the {@code char} value at {@code (index - 1)}
     * is in the low-surrogate range, {@code (index - 2)} is not
     * negative, and the {@code char} value at {@code (index -
     * 2)} is in the high-surrogate range, then the
     * supplementary code point value of the surrogate pair is
     * returned. If the {@code char} value at {@code index -
     * 1} is an unpaired low-surrogate or a high-surrogate, the
     * surrogate value is returned.
     *
     * @param     index the index following the code point that should be returned
     * @return    在给定的索引前的 Unicode 代码点
     * @exception IndexOutOfBoundsException 若索引值不在字符串的范围内
     * @since     1.5
     */
    public int codePointBefore(int index) {
        int i = index - 1;
        if ((i < 0) || (i >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointBeforeImpl(value, index, 0);
    }

    /**
     * 返回在该字符串中指定范围的 Unicode 代码点数量。文本的范围从 beginIndex 至
     * endIndex - 1。因此字符的长度为 endIndex-beginIndex。若 surrogate 不是
     * 一对地存在的，那么就会当做一个代码点。
     *
     * Returns the number of Unicode code points in the specified text
     * range of this {@code String}. The text range begins at the
     * specified {@code beginIndex} and extends to the
     * {@code char} at index {@code endIndex - 1}. Thus the
     * length (in {@code char}s) of the text range is
     * {@code endIndex-beginIndex}. Unpaired surrogates within
     * the text range count as one code point each.
     *
     * @param beginIndex 开始位置索引
     * @param endIndex 结束位置索引
     * @return 指定范围内，Unicode代码点的数量。
     * @exception IndexOutOfBoundsException 若 beginIndex、endIndex 不在字符串范围内
     * @since  1.5
     */
    public int codePointCount(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > value.length || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        return Character.codePointCountImpl(value, beginIndex, endIndex - beginIndex);
    }

    /**
     * 通过 index + 代码点偏移量，获取索引。若不是一对的 surrogate，
     * 那么就当做一个代码点。
     *
     * Returns the index within this {@code String} that is
     * offset from the given {@code index} by
     * {@code codePointOffset} code points. Unpaired surrogates
     * within the text range given by {@code index} and
     * {@code codePointOffset} count as one code point each.
     *
     * @param index 要偏移的索引
     * @param codePointOffset 代码点的偏移量
     * @return 该字符串的索引
     * @exception IndexOutOfBoundsException 若 index 不在字符串范围内,
     *   或者 codePointOffset > index，或者 codePointOffset 是负数
     *   并且索引前的子字符串小于codePointOffset的绝对值。
     * @since 1.5
     */
    public int offsetByCodePoints(int index, int codePointOffset) {
        if (index < 0 || index > value.length) {
            throw new IndexOutOfBoundsException();
        }
        return Character.offsetByCodePointsImpl(value, 0, value.length,
                index, codePointOffset);
    }

    /**
     * 从该字符串复制字符到从目标位置开始的目标的数组。
     * 该方法不会检查任何界限。
     *
     * Copy characters from this string into dst starting at dstBegin.
     * This method doesn't perform any range checking.
     */
    void getChars(char dst[], int dstBegin) {
        System.arraycopy(value, 0, dst, dstBegin, value.length);
    }

    /**
     * 从该字符串复制字符到目标字符数组。原理是 System#arraycopy
     *
     * Copies characters from this string into the destination character
     * array.
     *
     * <p>
     * 首个被复制的字符在索引 srcBegin 中；最后一个字符在索引 srcEnd-1 中
     * （因此被复制的字符总数为 srcEnd-srcBegin）。被复制的字符复制到目标数组 dest，
     * 从索引 destBengin 开始到结尾索引 (dstBegin + (srcEnd-srcBegin) - 1)。
     *
     * <p>
     * The first character to be copied is at index {@code srcBegin};
     * the last character to be copied is at index {@code srcEnd-1}
     * (thus the total number of characters to be copied is
     * {@code srcEnd-srcBegin}). The characters are copied into the
     * subarray of {@code dst} starting at index {@code dstBegin}
     * and ending at index:
     * <blockquote><pre>
     *     dstBegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @param      srcBegin   被复制字符串的开始字符的索引.
     * @param      srcEnd     被复制字符串的结束位置： srcEnd -1
     * @param      dst        目标数组.
     * @param      dstBegin   复制到目标数组的开始位置至最后
     * @exception IndexOutOfBoundsException If any of the following
     *            is true:
     *            <ul><li>{@code srcBegin} 是负数.
     *            <li>{@code srcBegin} 大于 {@code srcEnd}
     *            <li>{@code srcEnd} 超出数组界限
     *            <li>{@code dstBegin} 是负数
     *            <li>{@code dstBegin+(srcEnd-srcBegin)} 大于
     *                {@code dst.length}，也就是超出数组界限</ul>
     */
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if (srcEnd > value.length) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        }
        // 把 value[srcBegin] 到 value[srcBegin + (srcEnd-srcBegin) - 1] 范围的数据
        // 复制到 dst[dstBegin]开始至结束
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }

    /**
     * Copies characters from this string into the destination byte array. Each
     * byte receives the 8 low-order bits of the corresponding character. The
     * eight high-order bits of each character are not copied and do not
     * participate in the transfer in any way.
     *
     * <p> The first character to be copied is at index {@code srcBegin}; the
     * last character to be copied is at index {@code srcEnd-1}.  The total
     * number of characters to be copied is {@code srcEnd-srcBegin}. The
     * characters, converted to bytes, are copied into the subarray of {@code
     * dst} starting at index {@code dstBegin} and ending at index:
     *
     * <blockquote><pre>
     *     dstBegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @deprecated  This method does not properly convert characters into
     * bytes.  As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@link #getBytes()} method, which uses the platform's default charset.
     *
     * @param  srcBegin
     *         Index of the first character in the string to copy
     *
     * @param  srcEnd
     *         Index after the last character in the string to copy
     *
     * @param  dst
     *         The destination array
     *
     * @param  dstBegin
     *         The start offset in the destination array
     *
     * @throws  IndexOutOfBoundsException
     *          If any of the following is true:
     *          <ul>
     *            <li> {@code srcBegin} is negative
     *            <li> {@code srcBegin} is greater than {@code srcEnd}
     *            <li> {@code srcEnd} is greater than the length of this String
     *            <li> {@code dstBegin} is negative
     *            <li> {@code dstBegin+(srcEnd-srcBegin)} is larger than {@code
     *                 dst.length}
     *          </ul>
     */
    @Deprecated
    public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if (srcEnd > value.length) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        }
        Objects.requireNonNull(dst);

        int j = dstBegin;
        int n = srcEnd;
        int i = srcBegin;
        char[] val = value;   /* avoid getfield opcode */

        while (i < n) {
            dst[j++] = (byte)val[i++];
        }
    }

    /**
     * 使用指定的编码将字符串的一系列字节进行解码，并将结果保存道一个新的字节数组。
     *
     * Encodes this {@code String} into a sequence of bytes using the named
     * charset, storing the result into a new byte array.
     *
     * <p> The behavior of this method when this string cannot be encoded in
     * the given charset is unspecified.  The {@link
     * java.nio.charset.CharsetEncoder} class should be used when more control
     * over the encoding process is required.
     *
     * @param  charsetName
     *         字符编码需要被 {@linkplain java.nio.charset.Charset charset} 支持
     *
     * @return  结果字符数组
     *
     * @throws  UnsupportedEncodingException
     *          编码不支持
     *
     * @since  JDK1.1
     */
    public byte[] getBytes(String charsetName)
            throws UnsupportedEncodingException {
        if (charsetName == null) throw new NullPointerException();
        return StringCoding.encode(charsetName, value, 0, value.length);
    }

    /**
     * 通过给定的 Charset 类，对字符串一系列的字节进行解码，并将结果储存到一个新字节数组。
     *
     * Encodes this {@code String} into a sequence of bytes using the given
     * {@linkplain java.nio.charset.Charset charset}, storing the result into a
     * new byte array.
     *
     * <p> 该方法始终使用【默认替换数组】代替畸形输入和未处理的字符序列。
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement byte array.  The
     * {@link java.nio.charset.CharsetEncoder} class should be used when more
     * control over the encoding process is required.
     *
     * @param  charset
     *         The {@linkplain java.nio.charset.Charset} to be used to encode
     *         the {@code String}
     *
     * @return  The resultant byte array
     *
     * @since  1.6
     */
    public byte[] getBytes(Charset charset) {
        if (charset == null) throw new NullPointerException();
        return StringCoding.encode(charset, value, 0, value.length);
    }

    /**
     * 使用平台默认的编码对字符串一系列的字节进行解码，并将结果储存到一个新字节数组。
     *
     * Encodes this {@code String} into a sequence of bytes using the
     * platform's default charset, storing the result into a new byte array.
     *
     * <p> The behavior of this method when this string cannot be encoded in
     * the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetEncoder} class should be used when more control
     * over the encoding process is required.
     *
     * @return  The resultant byte array
     *
     * @since      JDK1.1
     */
    public byte[] getBytes() {
        return StringCoding.encode(value, 0, value.length);
    }

    /**
     * 通过指定对象，比较字符串的值。结果返回 true，仅为参数不为空，且是一个 String 对象时。
     *
     * Compares this string to the specified object.  The result is {@code
     * true} if and only if the argument is not {@code null} and is a {@code
     * String} object that represents the same sequence of characters as this
     * object.
     *
     * @param  anObject 字符串的比较值
     *
     * @return  若字符串对象与该字符串相等，则返回 true，否则 false.
     *
     * @see  #compareTo(String)
     * @see  #equalsIgnoreCase(String)
     */
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof String) {
            String anotherString = (String)anObject;
            int n = value.length;
            if (n == anotherString.value.length) {
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = 0;
                while (n-- != 0) {
                    if (v1[i] != v2[i])
                        return false;
                    i++;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 比较字符串与指定 StringBuffer 的值。
     * 有且只有当 String 与 StringBuffer 有相同的字符序列的时候，结果才返回 true。
     * 该方法通过 StringBuffer 实现同步语义。
     *
     * Compares this string to the specified {@code StringBuffer}.  The result
     * is {@code true} if and only if this {@code String} represents the same
     * sequence of characters as the specified {@code StringBuffer}. This method
     * synchronizes on the {@code StringBuffer}.
     *
     * @param  sb 与当前字符串进行比较的 StringBuffer 对象。
     *
     * @return  返回 true，代表有相同的字符序列；否则返回 false.
     *
     * @since  1.4
     */
    public boolean contentEquals(StringBuffer sb) {
        return contentEquals((CharSequence)sb);
    }

    private boolean nonSyncContentEquals(AbstractStringBuilder sb) {
        char v1[] = value;
        char v2[] = sb.getValue();
        int n = v1.length;
        if (n != sb.length()) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            if (v1[i] != v2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通过 CharSequence 对象比较当前字符串。
     * 有且只有当 String 与 CharSequence 有相同的字符序列的时候，结果才返回 true。
     * 注意：若 CharSequence 的实现类是 StringBuffer，则会实现同步。
     *
     * Compares this string to the specified {@code CharSequence}.  The
     * result is {@code true} if and only if this {@code String} represents the
     * same sequence of char values as the specified sequence. Note that if the
     * {@code CharSequence} is a {@code StringBuffer} then the method
     * synchronizes on it.
     *
     * @param  cs 与当前字符串进行比较的 CharSequence 对象
     *
     * @return  返回 true，代表有相同的字符序列；否则返回 false.
     *
     * @since  1.5
     */
    public boolean contentEquals(CharSequence cs) {
        // Argument is a StringBuffer, StringBuilder
        if (cs instanceof AbstractStringBuilder) {
            if (cs instanceof StringBuffer) {
                synchronized(cs) {
                   return nonSyncContentEquals((AbstractStringBuilder)cs);
                }
            } else {
                return nonSyncContentEquals((AbstractStringBuilder)cs);
            }
        }
        // Argument is a String
        if (cs instanceof String) {
            return equals(cs);
        }
        // Argument is a generic CharSequence
        char v1[] = value;
        int n = v1.length;
        if (n != cs.length()) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            if (v1[i] != cs.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较 Sting对象 与另一个 String对象，忽略大小写限制。
     *
     * Compares this {@code String} to another {@code String}, ignoring case
     * considerations.  Two strings are considered equal ignoring case if they
     * are of the same length and corresponding characters in the two strings
     * are equal ignoring case.
     *
     * <p> 两个字符序列 {@code c1} 和 {@code c2} 若遵守其一，则忽略大小写限制:
     * <ul>
     *   <li> 两个对象是一致的 (通过 == 进行比较后)
     *   <li> 每个字符都与 {@link java.lang.Character#toUpperCase(char)} 的结果一致.
     *   <li> 每个字符都与 {@link java.lang.Character#toLowerCase(char)} 的结果一致.
     * </ul>
     *
     * @param  anotherString 与当前 String对象 进行比较的 另一个String对象。
     *
     * @return  返回 true，代表有相同的字符序列；否则返回 false.
     *
     * @see  #equals(Object)
     */
    public boolean equalsIgnoreCase(String anotherString) {
        return (this == anotherString) ? true
                : (anotherString != null)
                && (anotherString.value.length == value.length)
                && regionMatches(true, 0, anotherString, 0, value.length);
    }

    /**
     * 按词典顺序比较两个字符串。该比较是基于字符串中，每个字符的 Unicode 值。
     * 若String对象在参数字符串（被比较的值）之前，则结果是负整数。
     * 若String对象在参数字符串之后，则结果是正整数。
     * 若结果相等，则返回零。
     * compareTo 方法返回 0 的时候，equals 方法也会返回 true.
     *
     * 例如：
     * String s1 = "abc";
     * String s2 = "abcdefg";
     * 那么，返回的结果是 -4；
     *
     * Compares two strings lexicographically.
     * The comparison is based on the Unicode value of each character in
     * the strings. The character sequence represented by this
     * {@code String} object is compared lexicographically to the
     * character sequence represented by the argument string. The result is
     * a negative integer if this {@code String} object
     * lexicographically precedes the argument string. The result is a
     * positive integer if this {@code String} object lexicographically
     * follows the argument string. The result is zero if the strings
     * are equal; {@code compareTo} returns {@code 0} exactly when
     * the {@link #equals(Object)} method would return {@code true}.
     *
     * <p>
     * 这是词典排序的定义。若两个字符串都是不同的，相同的索引的值肯定不同，或者长度不一致。
     * 如果它们在一个或多个索引位置上有不同的字符，则k是此类索引的最小值；
     * 然后，其在位置k处的字符具有较小值的字符串（通过使用 &lt; 运算符确定）
     * 按字典顺序位于另一个字符串之前。因此，compareTo 返回两个字符值在 k 值的差值。
     * <blockquote><pre>
     * this.charAt(k)-anotherString.charAt(k)
     * </pre></blockquote>
     *
     * 例如：
     * String s1 = "abcdefg";
     * String s2 = "abedefg";
     * c=99, e=101；返回的结果是 -2
     *
     * This is the definition of lexicographic ordering. If two strings are
     * different, then either they have different characters at some index
     * that is a valid index for both strings, or their lengths are different,
     * or both. If they have different characters at one or more index
     * positions, let <i>k</i> be the smallest such index; then the string
     * whose character at position <i>k</i> has the smaller value, as
     * determined by using the &lt; operator, lexicographically precedes the
     * other string. In this case, {@code compareTo} returns the
     * difference of the two character values at position {@code k} in
     * the two string -- that is, the value:
     * <blockquote><pre>
     * this.charAt(k)-anotherString.charAt(k)
     * </pre></blockquote>
     * If there is no index position at which they differ, then the shorter
     * string lexicographically precedes the longer string. In this case,
     * {@code compareTo} returns the difference of the lengths of the
     * strings -- that is, the value:
     * <blockquote><pre>
     * this.length()-anotherString.length()
     * </pre></blockquote>
     *
     * @param   anotherString 被比较的值.
     * @return  the value {@code 0} if the argument string is equal to
     *          this string; a value less than {@code 0} if this string
     *          is lexicographically less than the string argument; and a
     *          value greater than {@code 0} if this string is
     *          lexicographically greater than the string argument.
     */
    public int compareTo(String anotherString) {
        int len1 = value.length;
        int len2 = anotherString.value.length;
        int lim = Math.min(len1, len2);
        char v1[] = value;
        char v2[] = anotherString.value;

        int k = 0;
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }

    /**
     * 一个不区分大小写的比较器。该比较器是可序列化的。
     *
     * A Comparator that orders {@code String} objects as by
     * {@code compareToIgnoreCase}. This comparator is serializable.
     *
     * <p>
     * 注意：该比较器不会考虑语言环境，并且会导致某些区域设置的排序令人不满意。
     * java.text 包提供了 Collators 来允许区域设置的排序。
     *
     * Note that this Comparator does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>Collators</em> to allow
     * locale-sensitive ordering.
     *
     * @see     java.text.Collator#compare(String, String)
     * @since   1.2
     */
    public static final Comparator<String> CASE_INSENSITIVE_ORDER
                                         = new CaseInsensitiveComparator();
    private static class CaseInsensitiveComparator
            implements Comparator<String>, java.io.Serializable {
        // use serialVersionUID from JDK 1.2.2 for interoperability
        private static final long serialVersionUID = 8575799808933029326L;

        public int compare(String s1, String s2) {
            int n1 = s1.length();
            int n2 = s2.length();
            int min = Math.min(n1, n2);
            for (int i = 0; i < min; i++) {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(i);
                // 直接比较
                if (c1 != c2) {
                    // 大写比较
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    // 小写比较
                    if (c1 != c2) {
                        c1 = Character.toLowerCase(c1);
                        c2 = Character.toLowerCase(c2);
                        // 3次比较后都不相等，直接返回ASCII码差值
                        if (c1 != c2) {
                            // No overflow because of numeric promotion
                            return c1 - c2;
                        }
                    }
                }
            }
            // 若长度不等，返回长度差值.
            return n1 - n2;
        }

        /** Replaces the de-serialized object. */
        private Object readResolve() { return CASE_INSENSITIVE_ORDER; }
    }

    /**
     * 利用词典顺序，忽略大小写地比较字符串。该方法返回一个整数，通过对每个字符
     * 调用 toLowerCase 或 toUpperCase 进行比较。若不等，返回 ASCII 码差值。
     *
     * 原理是调用不区分大小写比较器的逻辑。
     *
     * Compares two strings lexicographically, ignoring case
     * differences. This method returns an integer whose sign is that of
     * calling {@code compareTo} with normalized versions of the strings
     * where case differences have been eliminated by calling
     * {@code Character.toLowerCase(Character.toUpperCase(character))} on
     * each character.
     *
     * <p>
     * 注意：该比较器不会考虑语言环境，并且会导致某些区域设置的排序令人不满意。
     * java.text 包提供了 Collators 来允许区域设置的排序。
     *
     * Note that this method does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>collators</em> to allow
     * locale-sensitive ordering.
     *
     * @param   str   被比较的字符串对象
     * @return  a negative integer, zero, or a positive integer as the
     *          specified String is greater than, equal to, or less
     *          than this String, ignoring case considerations.
     * @see     java.text.Collator#compare(String, String)
     * @since   1.2
     */
    public int compareToIgnoreCase(String str) {
        return CASE_INSENSITIVE_ORDER.compare(this, str);
    }

    /**
     * 测试两个字符串区域是否相等。
     * Tests if two string regions are equal.
     *
     * <p>
     * String 对象的一个子串，和另一个 String对象的子串进行比较。若子串都有相同的序列，
     * 则返回 true。String对象的子串范围在索引 toffset 到 (len + toffset - 1)，
     * 另一个被比较的 String对象的子串范围在索引 ooffset 到 (len + ooffset - 1).
     * 若出现结果为 false 的情况，则表示：
     * <ul><li>{@code toffset} 是负数.
     * <li>{@code ooffset} 是负数.
     * <li>{@code toffset+len} 大于 {@code String} 对象的长度.
     * <li>{@code ooffset+len} 大于另一个 {@code String} 对象的长度.
     * <li>非负整数 <i>k</i> 小于 {@code len}
     * 例如:
     * {@code this.charAt(toffset + }<i>k</i>{@code ) != other.charAt(ooffset + }
     * <i>k</i>{@code )}
     * </ul>
     *
     * A substring of this {@code String} object is compared to a substring
     * of the argument other. The result is true if these substrings
     * represent identical character sequences. The substring of this
     * {@code String} object to be compared begins at index {@code toffset}
     * and has length {@code len}. The substring of other to be compared
     * begins at index {@code ooffset} and has length {@code len}. The
     * result is {@code false} if and only if at least one of the following
     * is true:
     * <ul><li>{@code toffset} is negative.
     * <li>{@code ooffset} is negative.
     * <li>{@code toffset+len} is greater than the length of this
     * {@code String} object.
     * <li>{@code ooffset+len} is greater than the length of the other
     * argument.
     * <li>There is some nonnegative integer <i>k</i> less than {@code len}
     * such that:
     * {@code this.charAt(toffset + }<i>k</i>{@code ) != other.charAt(ooffset + }
     * <i>k</i>{@code )}
     * </ul>
     *
     * @param   toffset   当前字符串的开始索引
     * @param   other     另一个字符串.
     * @param   ooffset   另一个字符串的开始索引.
     * @param   len       字符比较数量.
     * @return  若匹配返回 true，否则 false;
     */
    public boolean regionMatches(int toffset, String other, int ooffset,
            int len) {
        char ta[] = value;
        int to = toffset;
        char pa[] = other.value;
        int po = ooffset;
        // Note: toffset, ooffset, or len might be near -1>>>1.
        if ((ooffset < 0) || (toffset < 0)
                || (toffset > (long)value.length - len)
                || (ooffset > (long)other.value.length - len)) {
            return false;
        }
        while (len-- > 0) {
            if (ta[to++] != pa[po++]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 测试两个字符串区域是否相等（是否忽略大小写版本）。
     * 注释和上面的都是一致的。
     * <p>
     * A substring of this {@code String} object is compared to a substring
     * of the argument {@code other}. The result is {@code true} if these
     * substrings represent character sequences that are the same, ignoring
     * case if and only if {@code ignoreCase} is true. The substring of
     * this {@code String} object to be compared begins at index
     * {@code toffset} and has length {@code len}. The substring of
     * {@code other} to be compared begins at index {@code ooffset} and
     * has length {@code len}. The result is {@code false} if and only if
     * at least one of the following is true:
     * <ul><li>{@code toffset} is negative.
     * <li>{@code ooffset} is negative.
     * <li>{@code toffset+len} is greater than the length of this
     * {@code String} object.
     * <li>{@code ooffset+len} is greater than the length of the other
     * argument.
     * <li>{@code ignoreCase} is {@code false} and there is some nonnegative
     * integer <i>k</i> less than {@code len} such that:
     * <blockquote><pre>
     * this.charAt(toffset+k) != other.charAt(ooffset+k)
     * </pre></blockquote>
     * <li>{@code ignoreCase} is {@code true} and there is some nonnegative
     * integer <i>k</i> less than {@code len} such that:
     * <blockquote><pre>
     * Character.toLowerCase(this.charAt(toffset+k)) !=
     Character.toLowerCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * and:
     * <blockquote><pre>
     * Character.toUpperCase(this.charAt(toffset+k)) !=
     *         Character.toUpperCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * </ul>
     *
     * @param   ignoreCase   if {@code true}, ignore case when comparing
     *                       characters.
     * @param   toffset      the starting offset of the subregion in this
     *                       string.
     * @param   other        the string argument.
     * @param   ooffset      the starting offset of the subregion in the string
     *                       argument.
     * @param   len          the number of characters to compare.
     * @return  {@code true} if the specified subregion of this string
     *          matches the specified subregion of the string argument;
     *          {@code false} otherwise. Whether the matching is exact
     *          or case insensitive depends on the {@code ignoreCase}
     *          argument.
     */
    public boolean regionMatches(boolean ignoreCase, int toffset,
            String other, int ooffset, int len) {
        char ta[] = value;
        int to = toffset;
        char pa[] = other.value;
        int po = ooffset;
        // Note: toffset, ooffset, or len might be near -1>>>1.
        if ((ooffset < 0) || (toffset < 0)
                || (toffset > (long)value.length - len)
                || (ooffset > (long)other.value.length - len)) {
            return false;
        }
        while (len-- > 0) {
            char c1 = ta[to++];
            char c2 = pa[po++];
            if (c1 == c2) {
                continue;
            }
            if (ignoreCase) {
                // If characters don't match but case may be ignored,
                // try converting both characters to uppercase.
                // If the results match, then the comparison scan should
                // continue.
                char u1 = Character.toUpperCase(c1);
                char u2 = Character.toUpperCase(c2);
                if (u1 == u2) {
                    continue;
                }
                // Unfortunately, conversion to uppercase does not work properly
                // for the Georgian alphabet, which has strange rules about case
                // conversion.  So we need to make one last check before
                // exiting.
                if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 测试字符串的子串是否以指定位置的前缀开始。
     *
     * Tests if the substring of this string beginning at the
     * specified index starts with the specified prefix.
     *
     * @param   prefix    前缀.
     * @param   toffset   开始位置.
     * @return  若字符序列匹配，则返回 true；否则返回 false；
     *          返回 false 也有可能是 toffset 为负数，或大于字符串长度。
     *          其他结果都与 <pre> this.substring(toffset).startsWith(prefix) </pre>一致。
     */
    public boolean startsWith(String prefix, int toffset) {
        char ta[] = value;
        int to = toffset;
        char pa[] = prefix.value;
        int po = 0;
        int pc = prefix.value.length;
        // Note: toffset might be near -1>>>1.
        if ((toffset < 0) || (toffset > value.length - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (ta[to++] != pa[po++]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 测试字符串的子串是否以指定前缀开始.
     * 原理：调用 startWith(String prefix, int toffset)，开始位置为0
     *
     * @param   prefix   前缀.
     * @return  若字符序列匹配，则返回 true；否则返回 false；
     *          注意：若前缀为空串，或相同引用（Object#equals校验后）
     *          也会返回 true.
     * @since   1. 0
     */
    public boolean startsWith(String prefix) {
        return startsWith(prefix, 0);
    }

    /**
     * 判断字符串是否以指定后缀结束.
     *
     * 原理：调用 startWith(String prefix, int toffset)，开始位置为 end - suffix.length
     *
     * @param   suffix   后缀.
     * @return 若字符序列匹配，则返回 true；否则返回 false；
     *         注意：若后缀为空串，或相同引用（Object#equals校验后）
     *         也会返回 true.
     */
    public boolean endsWith(String suffix) {
        return startsWith(suffix, value.length - suffix.value.length);
    }

    /**
     * 返回该对象的哈希值。
     *
     * String 对象的哈希值计算方式为:
     * <blockquote><pre>
     * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
     * </pre></blockquote>
     *
     * 使用 int 类型的算法，s[i] 是字符串第 [i] 个字符，n是字符串的长度，
     * ^ 表示指数。（空字符串的哈希值为 0）
     *
     * using {@code int} arithmetic, where {@code s[i]} is the
     * <i>i</i>th character of the string, {@code n} is the length of
     * the string, and {@code ^} indicates exponentiation.
     * (The hash value of the empty string is zero.)
     *
     * @return  该对象的哈希值.
     */
    public int hashCode() {
        int h = hash;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }

    /**
     * 返回字符串中指定字符第一次出现的索引。若一个字符值 ch 出现在该 String 对象
     * 的字符序列中，那么第一次出现的索引（Unicode编码单位）将会被返回。
     *
     * 1、对于字符值 ch 从0到0xFFFF（包括）范围，它是最小值k，因此 this.chat(k) 等于 ch。
     * 2、对于 ch 的其他值，它是最小值k，因此 this.codePointAt(k) 等于 ch。
     * 3、其他条件，若没有字符出现在该字符串中，则返回 -1。
     *
     * Returns the index within this string of the first occurrence of
     * the specified character. If a character with value
     * {@code ch} occurs in the character sequence represented by
     * this {@code String} object, then the index (in Unicode
     * code units) of the first such occurrence is returned. For
     * values of {@code ch} in the range from 0 to 0xFFFF
     * (inclusive), this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string, then {@code -1} is returned.
     *
     * @param   ch   字符 (Unicode 编码单元).
     * @return  返回该字符第一次出现的位置，若没有该字符，则返回 -1.
     */
    public int indexOf(int ch) {
        return indexOf(ch, 0);
    }

    /**
     * 返回字符串中指定字符第一次出现的索引，并从指定开始索引查找。
     *
     * Returns the index within this string of the first occurrence of the
     * specified character, starting the search at the specified index.
     *
     * <p>
     * 1、若字符值 ch 出现在该 String 对象中，并且所在的索引大于 fromIndex，那么
     *    该第一次出现的索引将会被返回。
     *
     * 2、对于 ch 值在 0 至 0xFFFF（包括）范围中，它是最小值 k，因此：
     *    this.charAt(k) == ch（k >= fromIndex）返回 true。
     *
     * 3、对于 ch 的其他值，它是最小值 k，因此：
     *    this.codePointAt(k) == ch （k >= fromIndex）返回 true。
     *
     * 4、在任意情况下，若没有这样的字符出现在字符串指定索引或后面的索引中，
     *    那么返回 -1。
     *
     * If a character with value {@code ch} occurs in the
     * character sequence represented by this {@code String}
     * object at an index no smaller than {@code fromIndex}, then
     * the index of the first such occurrence is returned. For values
     * of {@code ch} in the range from 0 to 0xFFFF (inclusive),
     * this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string at or after position {@code fromIndex}, then
     * {@code -1} is returned.
     *
     * <p>
     * fromIndex的值没有限制。
     * 1、若它是负数的，与值为零的时候有相同的作用：整个字符串将会被搜寻。
     * 2、若它大于该字符串的长度，与值为字符串的长度相同的作用：返回 -1。
     *
     * There is no restriction on the value of {@code fromIndex}. If it
     * is negative, it has the same effect as if it were zero: this entire
     * string may be searched. If it is greater than the length of this
     * string, it has the same effect as if it were equal to the length of
     * this string: {@code -1} is returned.
     *
     * <p>
     * 所有的索引都用 char 值（Unicode编码单元）定义。
     *
     * All indices are specified in {@code char} values
     * (Unicode code units).
     *
     * @param   ch          查找的字符 (Unicode编码单元).
     * @param   fromIndex   开始查找的索引位置。
     * @return  返回字符串中指定字符第一次出现的索引，并且索引 >= fromIndex,
     *          或者返回 {@code -1} 若这个字符没有出现。
     */
    public int indexOf(int ch, int fromIndex) {
        final int max = value.length;
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            // Note: fromIndex might be near -1>>>1.
            return -1;
        }

        // 若是小于65535，则直接遍历判断；
        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point))
            final char[] value = this.value;
            for (int i = fromIndex; i < max; i++) {
                if (value[i] == ch) {
                    return i;
                }
            }
            return -1;
        } else {
            // 若大于65535，则同时判断补充位
            return indexOfSupplementary(ch, fromIndex);
        }
    }

    /**
     * 处理带有补充字符的 indexOf 调用（很少用到）
     */
    private int indexOfSupplementary(int ch, int fromIndex) {
        if (Character.isValidCodePoint(ch)) {
            final char[] value = this.value;
            final char hi = Character.highSurrogate(ch);
            final char lo = Character.lowSurrogate(ch);
            final int max = value.length - 1;
            for (int i = fromIndex; i < max; i++) {
                // 这里 value[i+1] 的意思并非是下一个字符
                // 是因为一个超过65535的字符，必定会占数组的两个位置：高低Surrogate
                if (value[i] == hi && value[i + 1] == lo) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 返回在该字符串里的，最后一个出现的指定字符。
     *
     * 1、对于字符值 ch 从0到0xFFFF（包括）范围，this.chat(k) == ch 返回 true。
     * 2、对于 ch 的其他值，this.codePointAt(k) == ch 返回 true。
     * 3、其他条件，若该字符没有出现在该字符串中，则返回 -1。
     * 4、该方法是从最后的元素开始搜寻。
     *
     * Returns the index within this string of the last occurrence of
     * the specified character. For values of {@code ch} in the
     * range from 0 to 0xFFFF (inclusive), the index (in Unicode code
     * units) returned is the largest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * largest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true.  In either case, if no such character occurs in this
     * string, then {@code -1} is returned.  The
     * {@code String} is searched backwards starting at the last
     * character.
     *
     * @param   ch   搜寻的字符 (Unicode 编码单元).
     * @return  返回字符串中指定字符最后一次出现的索引，
     *          或者返回 {@code -1} 若这个字符没有出现。
     */
    public int lastIndexOf(int ch) {
        return lastIndexOf(ch, value.length - 1);
    }

    /**
     * 返回字符串中指定字符最后一次出现的索引，并从指定索引倒序查找。
     *
     * 1、对于 ch 值在 0 至 0xFFFF（包括）范围中：
     *    this.charAt(k) == ch（k >= fromIndex）返回 true。
     *
     * 2、对于 ch 的其他值：
     *    this.codePointAt(k) == ch （k >= fromIndex）返回 true。
     *
     * 3、在任意情况下，若没有这样的字符出现在字符串指定索引或后面的索引中，
     *    那么返回 -1。
     *
     * Returns the index within this string of the last occurrence of
     * the specified character, searching backward starting at the
     * specified index. For values of {@code ch} in the range
     * from 0 to 0xFFFF (inclusive), the index returned is the largest
     * value <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &lt;= fromIndex)
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * largest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &lt;= fromIndex)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string at or before position {@code fromIndex}, then
     * {@code -1} is returned.
     *
     * <p> 所有的索引都用 char 值（Unicode编码单元）定义
     *
     * @param   ch          搜寻的字符.
     * @param   fromIndex   开始搜索的索引。该值没有限制。
     *                      1、若 fromIndex >= length，与 <= length 有相同的结果：倒序搜索整个字符串。
     *                      2、若它是负数，与值为 -1 的结果相同：返回 -1。
     * @return  返回字符串中指定字符第一次出现的索引，并且索引 <= fromIndex；
     *          或者返回 {@code -1} 若这个字符没有出现。
     */
    public int lastIndexOf(int ch, int fromIndex) {
        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point))
            final char[] value = this.value;
            int i = Math.min(fromIndex, value.length - 1);
            for (; i >= 0; i--) {
                if (value[i] == ch) {
                    return i;
                }
            }
            return -1;
        } else {
            return lastIndexOfSupplementary(ch, fromIndex);
        }
    }

    /**
     * 处理带有补充字符的 indexOf 调用（很少用到）
     */
    private int lastIndexOfSupplementary(int ch, int fromIndex) {
        if (Character.isValidCodePoint(ch)) {
            final char[] value = this.value;
            char hi = Character.highSurrogate(ch);
            char lo = Character.lowSurrogate(ch);
            int i = Math.min(fromIndex, value.length - 2);
            for (; i >= 0; i--) {
                if (value[i] == hi && value[i + 1] == lo) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 返回字符串第一个出现的子串。
     * 原理：indexOf(String str, int fromIndex).
     *
     * Returns the index within this string of the first occurrence of the
     * specified substring.
     *
     * <p>返回的索引是最小的值 <i>k</i> :
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * 若 <i>k</i> 值不存在, 返回 {@code -1}.
     *
     * @param   str   搜索的子串.
     * @return  第一次出现的子串的索引，若不存在返回 -1.
     */
    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    /**
     * 返回字符串中指定字符第一次出现的索引，并从指定开始索引查找。
     * 原理：indexOf(
     *           char[] source, int sourceOffset, int sourceCount,
     *           String target, int fromIndex
     *      )
     *
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index.
     *
     * <p>返回的索引是最小的值 <i>k</i> :
     * <blockquote><pre>
     * <i>k</i> &gt;= fromIndex {@code &&} this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * 若 <i>k</i> 值不存在, 返回 {@code -1}.
     *
     * @param   str         搜索的子串.
     * @param   fromIndex   开始搜索的索引位置.
     * @return  返回子串第一次出现的开始位置，若不存在此子串，则返回 -1.
     */
    public int indexOf(String str, int fromIndex) {
        return indexOf(value, 0, value.length,
                str.value, 0, str.value.length, fromIndex);
    }

    /**
     * 此方法是字符串和 AbstractStringBuilder 共享的代码，用于执行搜索。
     * source 数组是被搜索的字符数组，target 字符串是要匹配的子串。
     *
     * 原理：
     * indexOf(char[] source, int sourceOffset, int sourceCount,
     *         char[] target, int targetOffset, int targetCount,
     *         int fromIndex)
     *
     * Code shared by String and AbstractStringBuilder to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source       被搜索的字符数组.
     * @param   sourceOffset 字符数组的开始位置.
     * @param   sourceCount  字符数组的长度.
     * @param   target       将要匹配的子串.
     * @param   fromIndex    开始搜索的索引位置.
     */
    static int indexOf(char[] source, int sourceOffset, int sourceCount,
            String target, int fromIndex) {
        return indexOf(source, sourceOffset, sourceCount,
                       target.value, 0, target.value.length,
                       fromIndex);
    }

    /**
     * 此方法是字符串和 AbstractStringBuilder 共享的代码，用于执行搜索。
     * source 数组是被搜索的字符数组，target 字符串是要匹配的子串。
     *
     * Code shared by String and StringBuffer to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source       搜索的字符数组.
     * @param   sourceOffset 字符数组的开始位置.
     * @param   sourceCount  字符数组的长度.
     * @param   target       将要匹配的子串.
     * @param   targetOffset 子串的开始位置.
     * @param   targetCount  子串的长度.
     * @param   fromIndex    开始搜索的位置.
     */
    static int indexOf(char[] source, int sourceOffset, int sourceCount,
            char[] target, int targetOffset, int targetCount,
            int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j]
                        == target[k]; j++, k++);

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    /**
     * 返回最后一个出现目标子串的下标。
     * 最后出现的空串 "" 被认为在下标 {@code this.length()} 处。
     * 例如："abcd".lastIndexOf(""); 返回值为 4.
     *
     * Returns the index within this string of the last occurrence of the
     * specified substring.  The last occurrence of the empty string ""
     * is considered to occur at the index value {@code this.length()}.
     *
     * （下面这句不翻译，对于我来说是废话）
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str   搜寻的子串.
     * @return  最后出现该子串的索引位置，若没有该子串则返回 -1.
     */
    public int lastIndexOf(String str) {
        return lastIndexOf(str, value.length);
    }

    /**
     * 返回最后一个出现目标子串的下标。倒序地从指定位置开始搜索。
     *
     * Returns the index within this string of the last occurrence of the
     * specified substring, searching backward starting at the specified index.
     *
     * （下面这句不翻译，对于我来说是废话）
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * <i>k</i> {@code <=} fromIndex {@code &&} this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str         查找的子串.
     * @param   fromIndex   倒序开始查找的位置.
     * @return  最后出现该子串的索引位置，若没有该子串则返回 -1.
     */
    public int lastIndexOf(String str, int fromIndex) {
        return lastIndexOf(value, 0, value.length,
                str.value, 0, str.value.length, fromIndex);
    }

    /**
     * 此方法是字符串和 AbstractStringBuilder 共享的代码，用于执行搜索。
     * source 数组是被搜索的字符数组，target 字符串是要匹配的子串。
     *
     * Code shared by String and AbstractStringBuilder to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source       搜索的字符数组.
     * @param   sourceOffset 字符数组的开始位置.
     * @param   sourceCount  字符数组的长度.
     * @param   target       将要匹配的子串.
     * @param   fromIndex    从指定索引开始查找.
     */
    static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
            String target, int fromIndex) {
        return lastIndexOf(source, sourceOffset, sourceCount,
                       target.value, 0, target.value.length,
                       fromIndex);
    }

    /**
     * 此方法是字符串和 AbstractStringBuilder 共享的代码，用于执行搜索。
     * source 数组是被搜索的字符数组，target 字符串是要匹配的子串。
     *
     * Code shared by String and StringBuffer to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source       搜索的字符数组.
     * @param   sourceOffset 字符数组的开始位置.
     * @param   sourceCount  字符数组的长度.
     * @param   target       将要匹配的子串.
     * @param   targetOffset 子串开始位置.
     * @param   targetCount  子串长度.
     * @param   fromIndex    从指定索引开始查找.
     */
    static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
            char[] target, int targetOffset, int targetCount,
            int fromIndex) {
        /*
         * Check arguments; return immediately where possible. For
         * consistency, don't check for null str.
         */
        int rightIndex = sourceCount - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        // 调整开始索引
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* 若空串，返回长度 */
        if (targetCount == 0) {
            return fromIndex;
        }

        int strLastIndex = targetOffset + targetCount - 1;
        char strLastChar = target[strLastIndex];
        int min = sourceOffset + targetCount - 1;
        int i = min + fromIndex;

        startSearchForLastChar:
        while (true) {
            // 先查找子串最后一个字符
            while (i >= min && source[i] != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            // 查找子串剩余字符
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (source[j--] != target[k--]) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start - sourceOffset + 1;
        }
    }

    /**
     * 返回该字符串的子串。子串从指定索引的字符开始，直至该字符串末尾。
     * 例子:
     * <blockquote><pre>
     * "unhappy".substring(2) 返回 "happy"
     * "Harbison".substring(3) 返回 "bison"
     * "emptiness".substring(9) 返回 "" (空字符串)
     * </pre></blockquote>
     *
     * Returns a string that is a substring of this string. The
     * substring begins with the character at the specified index and
     * extends to the end of this string. <p>
     * Examples:
     * <blockquote><pre>
     * "unhappy".substring(2) returns "happy"
     * "Harbison".substring(3) returns "bison"
     * "emptiness".substring(9) returns "" (an empty string)
     * </pre></blockquote>
     *
     * @param      beginIndex   开始索引，包括在内.
     * @return     指定范围内的子串.
     * @exception  IndexOutOfBoundsException  若
     *             {@code beginIndex} 为负数，或者大于 {@code String} 的长度.
     */
    public String substring(int beginIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        int subLen = value.length - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        return (beginIndex == 0) ? this : new String(value, beginIndex, subLen);
    }

    /**
     * 返回字符串中的子串。从指定索引开始，到 结束索引-1 的范围。
     * 因此子串的长度为 {@code endIndex-beginIndex}。
     * <p>
     * 例子:
     * <blockquote><pre>
     * "hamburger".substring(4, 8) 返回 "urge"
     * "smiles".substring(1, 5) 返回 "mile"
     * </pre></blockquote>
     *
     * Returns a string that is a substring of this string. The
     * substring begins at the specified {@code beginIndex} and
     * extends to the character at index {@code endIndex - 1}.
     * Thus the length of the substring is {@code endIndex-beginIndex}.
     * <p>
     * Examples:
     * <blockquote><pre>
     * "hamburger".substring(4, 8) returns "urge"
     * "smiles".substring(1, 5) returns "mile"
     * </pre></blockquote>
     *
     * @param      beginIndex   开始索引，包括在内.
     * @param      endIndex     结束索引，不包括.
     * @return     指定范围内的子串.
     * @exception  IndexOutOfBoundsException 如果
     *             {@code beginIndex} 是负数, 或者
     *             {@code endIndex} 大于 {@code String} 的长度,
     *             或者 {@code beginIndex} 大于 {@code endIndex}.
     */
    public String substring(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > value.length) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        return ((beginIndex == 0) && (endIndex == value.length)) ? this
                : new String(value, beginIndex, subLen);
    }

    /**
     * 返回一个该序列的字符子序列。
     *
     * <p> 调用此方法的形式：
     *
     * <blockquote><pre>
     * str.subSequence(begin,&nbsp;end)</pre></blockquote>
     *
     * 与下面的方式异曲同工：
     *
     * <blockquote><pre>
     * str.substring(begin,&nbsp;end)</pre></blockquote>
     *
     * @apiNote
     * 该方法的定义，以便 String类 可以实现 CharSequence 接口。
     *
     * @param   beginIndex   开始索引（包含）.
     * @param   endIndex     结束索引（不包含）.
     * @return  指定的子序列.
     *
     * @throws  IndexOutOfBoundsException
     *          若 {@code beginIndex} 或 {@code endIndex} 是负数，
     *          若 {@code endIndex} 大于 {@code length()}，或
     *             {@code beginIndex} 大于 {@code endIndex}
     *
     * @since 1.4
     * @spec JSR-51
     */
    public CharSequence subSequence(int beginIndex, int endIndex) {
        return this.substring(beginIndex, endIndex);
    }

    /**
     * 连接指定字符串至该字符串的末尾。
     *
     * <p>
     *     若参数上的字符串长度是0，则就会返回 {@code String} 的内容。
     * 该方法首先将原字符串转换为数组，然后使用 System.arrayCopy 将 str
     * 复制至原数组的末尾，然后再转换为 String。
     *
     * 例子:
     * <blockquote><pre>
     * "cares".concat("s") 返回 "caress"
     * "to".concat("get").concat("her") 返回 "together"
     * </pre></blockquote>
     *
     * @param   str   被拼接至末端的字符串
     * @return  原字符串+被拼接的字符串
     */
    public String concat(String str) {
        int otherLen = str.length();
        if (otherLen == 0) {
            return this;
        }
        int len = value.length;
        char buf[] = Arrays.copyOf(value, len + otherLen);
        str.getChars(buf, len);
        return new String(buf, true);
    }

    /**
     * 返回被替换指定字符后的字符串结果。
     * <p>
     *     若旧字符没有出现在该字符序列中，则返回该字符串的引用。
     *
     * <p>
     * 例子:
     * <blockquote><pre>
     * "mesquite in your cellar".replace('e', 'o')
     *         返回 "mosquito in your collar"
     * "the war of baronets".replace('r', 'y')
     *         返回 "the way of bayonets"
     * "sparring with a purple porpoise".replace('p', 't')
     *         返回 "starring with a turtle tortoise"
     * "JonL".replace('q', 'x') 返回 "JonL" (no change)
     * </pre></blockquote>
     *
     * @param   oldChar   被替换的旧字符.
     * @param   newChar   新字符.
     * @return  通过使用新字符替换旧字符，来派生出新的字符串
     */
    public String replace(char oldChar, char newChar) {
        if (oldChar != newChar) {
            int len = value.length;
            int i = -1;
            char[] val = value; /* avoid getfield opcode */

            // 获取第一次出现需要替换的元素的位置
            while (++i < len) {
                if (val[i] == oldChar) {
                    break;
                }
            }
            // 若有需要替换的元素，则进行替换
            // 不过，要先将 i 之前的所有元素复制过去新数组
            if (i < len) {
                char buf[] = new char[len];
                for (int j = 0; j < i; j++) {
                    buf[j] = val[j];
                }
                while (i < len) {
                    char c = val[i];
                    buf[i] = (c == oldChar) ? newChar : c;
                    i++;
                }
                return new String(buf, true);
            }
        }
        return this;
    }

    /**
     * 告知该字符串是否存在给定 <a href="../util/regex/Pattern.html#sum">
     *     正则表达式 </a> 匹配的内容.
     *
     * <p> 该方法与下面的操作异曲同工
     * <i>str</i>{@code .matches(}<i>regex</i>{@code )}
     *
     * <blockquote>
     * {@link java.util.regex.Pattern}.{@link java.util.regex.Pattern#matches(String,CharSequence)
     * matches(<i>regex</i>, <i>str</i>)}
     * </blockquote>
     *
     * @param   regex
     *          将匹配此字符串的正则表达式
     *
     * @return  若返回 {@code true}, 仅此只有 true，那么该字符串匹配该正则表达式.
     *
     * @throws  PatternSyntaxException
     *          若正则表达式不合法.
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     */
    public boolean matches(String regex) {
        return Pattern.matches(regex, this);
    }

    /**
     * 当且仅当该字符串序列包含该字符串时，才返回 true.
     *
     * @param s 查找的字符串
     * @return 当且仅当查找到的时候，才返回 true.
     * @since 1.5
     */
    public boolean contains(CharSequence s) {
        return indexOf(s.toString()) > -1;
    }

    /**
     * 使用正则表达式找出指定字符串，然后使用新字符串（不是字符）替换第一次出现的旧字符串。
     *
     * <p>
     * <i>str</i>{@code .replaceFirst(}<i>regex</i>{@code ,} <i>repl</i>{@code )}
     *
     * 该方法与下面的操作异曲同工
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
     * java.util.regex.Matcher#replaceFirst replaceFirst}(<i>repl</i>)
     * </code>
     * </blockquote>
     *
     *<p>
     * 请注意，反斜杠 ({@code \}) 和 美元符号({@code $}) 在替换字符串中
     * 可能导致结果，与被视为文字替换字符串的结果不同。
     *
     * {@link java.util.regex.Matcher#replaceFirst}.
     * 若需要，请使用 {@link java.util.regex.Matcher#quoteReplacement} 来抑制这些字符的特殊含义。
     *
     * @param   regex 查找匹配内容的正则表达式
     * @param   replacement 新字符串，用于替换第一个匹配到的旧字符串
     *
     * @return  {@code String} 结果.
     *
     * @throws  PatternSyntaxException
     *          若正则表达式的语法错误
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     */
    public String replaceFirst(String regex, String replacement) {
        return Pattern.compile(regex).matcher(this).replaceFirst(replacement);
    }

    /**
     * 使用正则表达式找出指定字符串，然后使用新字符串（不是字符）替换所有出现过的旧字符串。
     *
     * <p>
     * <i>str</i>{@code .replaceAll(}<i>regex</i>{@code ,} <i>repl</i>{@code )}
     *
     * 该方法与下面的操作异曲同工
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
     * java.util.regex.Matcher#replaceAll replaceAll}(<i>repl</i>)
     * </code>
     * </blockquote>
     *
     *<p>
     * 请注意，反斜杠 ({@code \}) 和 美元符号({@code $}) 在替换字符串中
     * 可能导致结果，与被视为文字替换字符串的结果不同。
     * {@link java.util.regex.Matcher#replaceFirst}.
     * 若需要，请使用 {@link java.util.regex.Matcher#quoteReplacement} 来抑制这些字符的特殊含义。
     *
     * @param   regex
     *          查找匹配内容的正则表达式
     * @param   replacement
     *          新字符串，用于替换第匹配到的所有旧字符串
     *
     * @return  {@code String}结果。
     *
     * @throws  PatternSyntaxException
     *          若正则表达式的语法错误.
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     */
    public String replaceAll(String regex, String replacement) {
        return Pattern.compile(regex).matcher(this).replaceAll(replacement);
    }

    /**
     * 使用新的字符串序列，替换旧的字符串序列。
     * 若有多个相同的，则只替换最开始的子串。
     * 例如：
     * "aaa"：使用 "b"，替换掉 "aa"，则返回 "ba"，而不是 "ab".
     *
     * @param  target 目标被替换的子串
     * @param  replacement 用于替换旧子串的新子串
     * @return  结果字符串
     * @since 1.5
     */
    public String replace(CharSequence target, CharSequence replacement) {
        return Pattern.compile(target.toString(), Pattern.LITERAL).matcher(
                this).replaceAll(Matcher.quoteReplacement(replacement.toString()));
    }

    /**
     * Splits this string around matches of the given
     * <a href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * <p> The array returned by this method contains each substring of this
     * string that is terminated by another substring that matches the given
     * expression or is terminated by the end of the string.  The substrings in
     * the array are in the order in which they occur in this string.  If the
     * expression does not match any part of the input then the resulting array
     * has just one element, namely this string.
     *
     * <p> When there is a positive-width match at the beginning of this
     * string then an empty leading substring is included at the beginning
     * of the resulting array. A zero-width match at the beginning however
     * never produces such empty leading substring.
     *
     * <p> The {@code limit} parameter controls the number of times the
     * pattern is applied and therefore affects the length of the resulting
     * array.  If the limit <i>n</i> is greater than zero then the pattern
     * will be applied at most <i>n</i>&nbsp;-&nbsp;1 times, the array's
     * length will be no greater than <i>n</i>, and the array's last entry
     * will contain all input beyond the last matched delimiter.  If <i>n</i>
     * is non-positive then the pattern will be applied as many times as
     * possible and the array can have any length.  If <i>n</i> is zero then
     * the pattern will be applied as many times as possible, the array can
     * have any length, and trailing empty strings will be discarded.
     *
     * <p> The string {@code "boo:and:foo"}, for example, yields the
     * following results with these parameters:
     *
     * <blockquote><table cellpadding=1 cellspacing=0 summary="Split example showing regex, limit, and result">
     * <tr>
     *     <th>Regex</th>
     *     <th>Limit</th>
     *     <th>Result</th>
     * </tr>
     * <tr><td align=center>:</td>
     *     <td align=center>2</td>
     *     <td>{@code { "boo", "and:foo" }}</td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>5</td>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>-2</td>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>5</td>
     *     <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>-2</td>
     *     <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>0</td>
     *     <td>{@code { "b", "", ":and:f" }}</td></tr>
     * </table></blockquote>
     *
     * <p> An invocation of this method of the form
     * <i>str.</i>{@code split(}<i>regex</i>{@code ,}&nbsp;<i>n</i>{@code )}
     * yields the same result as the expression
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#split(java.lang.CharSequence,int) split}(<i>str</i>,&nbsp;<i>n</i>)
     * </code>
     * </blockquote>
     *
     *
     * @param  regex
     *         the delimiting regular expression
     *
     * @param  limit
     *         the result threshold, as described above
     *
     * @return  the array of strings computed by splitting this string
     *          around matches of the given regular expression
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     */
    public String[] split(String regex, int limit) {
        /* fastpath if the regex is a
         (1)one-char String and this character is not one of the
            RegEx's meta characters ".$|()[{^?*+\\", or
         (2)two-char String and the first char is the backslash and
            the second is not the ascii digit or ascii letter.
         */
        char ch = 0;
        if (((regex.value.length == 1 &&
             ".$|()[{^?*+\\".indexOf(ch = regex.charAt(0)) == -1) ||
             (regex.length() == 2 &&
              regex.charAt(0) == '\\' &&
              (((ch = regex.charAt(1))-'0')|('9'-ch)) < 0 &&
              ((ch-'a')|('z'-ch)) < 0 &&
              ((ch-'A')|('Z'-ch)) < 0)) &&
            (ch < Character.MIN_HIGH_SURROGATE ||
             ch > Character.MAX_LOW_SURROGATE))
        {
            int off = 0;
            int next = 0;
            boolean limited = limit > 0;
            ArrayList<String> list = new ArrayList<>();
            while ((next = indexOf(ch, off)) != -1) {
                if (!limited || list.size() < limit - 1) {
                    list.add(substring(off, next));
                    off = next + 1;
                } else {    // last one
                    //assert (list.size() == limit - 1);
                    list.add(substring(off, value.length));
                    off = value.length;
                    break;
                }
            }
            // If no match was found, return this
            if (off == 0)
                return new String[]{this};

            // Add remaining segment
            if (!limited || list.size() < limit)
                list.add(substring(off, value.length));

            // Construct result
            int resultSize = list.size();
            if (limit == 0) {
                while (resultSize > 0 && list.get(resultSize - 1).length() == 0) {
                    resultSize--;
                }
            }
            String[] result = new String[resultSize];
            return list.subList(0, resultSize).toArray(result);
        }
        return Pattern.compile(regex).split(this, limit);
    }

    /**
     * Splits this string around matches of the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * <p> This method works as if by invoking the two-argument {@link
     * #split(String, int) split} method with the given expression and a limit
     * argument of zero.  Trailing empty strings are therefore not included in
     * the resulting array.
     *
     * <p> The string {@code "boo:and:foo"}, for example, yields the following
     * results with these expressions:
     *
     * <blockquote><table cellpadding=1 cellspacing=0 summary="Split examples showing regex and result">
     * <tr>
     *  <th>Regex</th>
     *  <th>Result</th>
     * </tr>
     * <tr><td align=center>:</td>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td>{@code { "b", "", ":and:f" }}</td></tr>
     * </table></blockquote>
     *
     *
     * @param  regex
     *         the delimiting regular expression
     *
     * @return  the array of strings computed by splitting this string
     *          around matches of the given regular expression
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     */
    public String[] split(String regex) {
        return split(regex, 0);
    }

    /**
     * Returns a new String composed of copies of the
     * {@code CharSequence elements} joined together with a copy of
     * the specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     String message = String.join("-", "Java", "is", "cool");
     *     // message returned is: "Java-is-cool"
     * }</pre></blockquote>
     *
     * Note that if an element is null, then {@code "null"} is added.
     *
     * @param  delimiter the delimiter that separates each element
     * @param  elements the elements to join together.
     *
     * @return a new {@code String} that is composed of the {@code elements}
     *         separated by the {@code delimiter}
     *
     * @throws NullPointerException If {@code delimiter} or {@code elements}
     *         is {@code null}
     *
     * @see java.util.StringJoiner
     * @since 1.8
     */
    public static String join(CharSequence delimiter, CharSequence... elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        // Number of elements not likely worth Arrays.stream overhead.
        StringJoiner joiner = new StringJoiner(delimiter);
        for (CharSequence cs: elements) {
            joiner.add(cs);
        }
        return joiner.toString();
    }

    /**
     * Returns a new {@code String} composed of copies of the
     * {@code CharSequence elements} joined together with a copy of the
     * specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     List<String> strings = new LinkedList<>();
     *     strings.add("Java");strings.add("is");
     *     strings.add("cool");
     *     String message = String.join(" ", strings);
     *     //message returned is: "Java is cool"
     *
     *     Set<String> strings = new LinkedHashSet<>();
     *     strings.add("Java"); strings.add("is");
     *     strings.add("very"); strings.add("cool");
     *     String message = String.join("-", strings);
     *     //message returned is: "Java-is-very-cool"
     * }</pre></blockquote>
     *
     * Note that if an individual element is {@code null}, then {@code "null"} is added.
     *
     * @param  delimiter a sequence of characters that is used to separate each
     *         of the {@code elements} in the resulting {@code String}
     * @param  elements an {@code Iterable} that will have its {@code elements}
     *         joined together.
     *
     * @return a new {@code String} that is composed from the {@code elements}
     *         argument
     *
     * @throws NullPointerException If {@code delimiter} or {@code elements}
     *         is {@code null}
     *
     * @see    #join(CharSequence,CharSequence...)
     * @see    java.util.StringJoiner
     * @since 1.8
     */
    public static String join(CharSequence delimiter,
            Iterable<? extends CharSequence> elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        StringJoiner joiner = new StringJoiner(delimiter);
        for (CharSequence cs: elements) {
            joiner.add(cs);
        }
        return joiner.toString();
    }

    /**
     * Converts all of the characters in this {@code String} to lower
     * case using the rules of the given {@code Locale}.  Case mapping is based
     * on the Unicode Standard version specified by the {@link java.lang.Character Character}
     * class. Since case mappings are not always 1:1 char mappings, the resulting
     * {@code String} may be a different length than the original {@code String}.
     * <p>
     * Examples of lowercase  mappings are in the following table:
     * <table border="1" summary="Lowercase mapping examples showing language code of locale, upper case, lower case, and description">
     * <tr>
     *   <th>Language Code of Locale</th>
     *   <th>Upper Case</th>
     *   <th>Lower Case</th>
     *   <th>Description</th>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0130</td>
     *   <td>&#92;u0069</td>
     *   <td>capital letter I with dot above -&gt; small letter i</td>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0049</td>
     *   <td>&#92;u0131</td>
     *   <td>capital letter I -&gt; small letter dotless i </td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td>French Fries</td>
     *   <td>french fries</td>
     *   <td>lowercased all chars in String</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td><img src="doc-files/capiota.gif" alt="capiota"><img src="doc-files/capchi.gif" alt="capchi">
     *       <img src="doc-files/captheta.gif" alt="captheta"><img src="doc-files/capupsil.gif" alt="capupsil">
     *       <img src="doc-files/capsigma.gif" alt="capsigma"></td>
     *   <td><img src="doc-files/iota.gif" alt="iota"><img src="doc-files/chi.gif" alt="chi">
     *       <img src="doc-files/theta.gif" alt="theta"><img src="doc-files/upsilon.gif" alt="upsilon">
     *       <img src="doc-files/sigma1.gif" alt="sigma"></td>
     *   <td>lowercased all chars in String</td>
     * </tr>
     * </table>
     *
     * @param locale use the case transformation rules for this locale
     * @return the {@code String}, converted to lowercase.
     * @see     java.lang.String#toLowerCase()
     * @see     java.lang.String#toUpperCase()
     * @see     java.lang.String#toUpperCase(Locale)
     * @since   1.1
     */
    public String toLowerCase(Locale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }

        int firstUpper;
        final int len = value.length;

        /* Now check if there are any characters that need to be changed. */
        scan: {
            for (firstUpper = 0 ; firstUpper < len; ) {
                char c = value[firstUpper];
                if ((c >= Character.MIN_HIGH_SURROGATE)
                        && (c <= Character.MAX_HIGH_SURROGATE)) {
                    int supplChar = codePointAt(firstUpper);
                    if (supplChar != Character.toLowerCase(supplChar)) {
                        break scan;
                    }
                    firstUpper += Character.charCount(supplChar);
                } else {
                    if (c != Character.toLowerCase(c)) {
                        break scan;
                    }
                    firstUpper++;
                }
            }
            return this;
        }

        char[] result = new char[len];
        int resultOffset = 0;  /* result may grow, so i+resultOffset
                                * is the write location in result */

        /* Just copy the first few lowerCase characters. */
        System.arraycopy(value, 0, result, 0, firstUpper);

        String lang = locale.getLanguage();
        boolean localeDependent =
                (lang == "tr" || lang == "az" || lang == "lt");
        char[] lowerCharArray;
        int lowerChar;
        int srcChar;
        int srcCount;
        for (int i = firstUpper; i < len; i += srcCount) {
            srcChar = (int)value[i];
            if ((char)srcChar >= Character.MIN_HIGH_SURROGATE
                    && (char)srcChar <= Character.MAX_HIGH_SURROGATE) {
                srcChar = codePointAt(i);
                srcCount = Character.charCount(srcChar);
            } else {
                srcCount = 1;
            }
            if (localeDependent ||
                srcChar == '\u03A3' || // GREEK CAPITAL LETTER SIGMA
                srcChar == '\u0130') { // LATIN CAPITAL LETTER I WITH DOT ABOVE
                lowerChar = ConditionalSpecialCasing.toLowerCaseEx(this, i, locale);
            } else {
                lowerChar = Character.toLowerCase(srcChar);
            }
            if ((lowerChar == Character.ERROR)
                    || (lowerChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
                if (lowerChar == Character.ERROR) {
                    lowerCharArray =
                            ConditionalSpecialCasing.toLowerCaseCharArray(this, i, locale);
                } else if (srcCount == 2) {
                    resultOffset += Character.toChars(lowerChar, result, i + resultOffset) - srcCount;
                    continue;
                } else {
                    lowerCharArray = Character.toChars(lowerChar);
                }

                /* Grow result if needed */
                int mapLen = lowerCharArray.length;
                if (mapLen > srcCount) {
                    char[] result2 = new char[result.length + mapLen - srcCount];
                    System.arraycopy(result, 0, result2, 0, i + resultOffset);
                    result = result2;
                }
                for (int x = 0; x < mapLen; ++x) {
                    result[i + resultOffset + x] = lowerCharArray[x];
                }
                resultOffset += (mapLen - srcCount);
            } else {
                result[i + resultOffset] = (char)lowerChar;
            }
        }
        return new String(result, 0, len + resultOffset);
    }

    /**
     * Converts all of the characters in this {@code String} to lower
     * case using the rules of the default locale. This is equivalent to calling
     * {@code toLowerCase(Locale.getDefault())}.
     * <p>
     * <b>Note:</b> This method is locale sensitive, and may produce unexpected
     * results if used for strings that are intended to be interpreted locale
     * independently.
     * Examples are programming language identifiers, protocol keys, and HTML
     * tags.
     * For instance, {@code "TITLE".toLowerCase()} in a Turkish locale
     * returns {@code "t\u005Cu0131tle"}, where '\u005Cu0131' is the
     * LATIN SMALL LETTER DOTLESS I character.
     * To obtain correct results for locale insensitive strings, use
     * {@code toLowerCase(Locale.ROOT)}.
     * <p>
     * @return  the {@code String}, converted to lowercase.
     * @see     java.lang.String#toLowerCase(Locale)
     */
    public String toLowerCase() {
        return toLowerCase(Locale.getDefault());
    }

    /**
     * Converts all of the characters in this {@code String} to upper
     * case using the rules of the given {@code Locale}. Case mapping is based
     * on the Unicode Standard version specified by the {@link java.lang.Character Character}
     * class. Since case mappings are not always 1:1 char mappings, the resulting
     * {@code String} may be a different length than the original {@code String}.
     * <p>
     * Examples of locale-sensitive and 1:M case mappings are in the following table.
     *
     * <table border="1" summary="Examples of locale-sensitive and 1:M case mappings. Shows Language code of locale, lower case, upper case, and description.">
     * <tr>
     *   <th>Language Code of Locale</th>
     *   <th>Lower Case</th>
     *   <th>Upper Case</th>
     *   <th>Description</th>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0069</td>
     *   <td>&#92;u0130</td>
     *   <td>small letter i -&gt; capital letter I with dot above</td>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0131</td>
     *   <td>&#92;u0049</td>
     *   <td>small letter dotless i -&gt; capital letter I</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td>&#92;u00df</td>
     *   <td>&#92;u0053 &#92;u0053</td>
     *   <td>small letter sharp s -&gt; two letters: SS</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td>Fahrvergn&uuml;gen</td>
     *   <td>FAHRVERGN&Uuml;GEN</td>
     *   <td></td>
     * </tr>
     * </table>
     * @param locale use the case transformation rules for this locale
     * @return the {@code String}, converted to uppercase.
     * @see     java.lang.String#toUpperCase()
     * @see     java.lang.String#toLowerCase()
     * @see     java.lang.String#toLowerCase(Locale)
     * @since   1.1
     */
    public String toUpperCase(Locale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }

        int firstLower;
        final int len = value.length;

        /* Now check if there are any characters that need to be changed. */
        scan: {
            for (firstLower = 0 ; firstLower < len; ) {
                int c = (int)value[firstLower];
                int srcCount;
                if ((c >= Character.MIN_HIGH_SURROGATE)
                        && (c <= Character.MAX_HIGH_SURROGATE)) {
                    c = codePointAt(firstLower);
                    srcCount = Character.charCount(c);
                } else {
                    srcCount = 1;
                }
                int upperCaseChar = Character.toUpperCaseEx(c);
                if ((upperCaseChar == Character.ERROR)
                        || (c != upperCaseChar)) {
                    break scan;
                }
                firstLower += srcCount;
            }
            return this;
        }

        /* result may grow, so i+resultOffset is the write location in result */
        int resultOffset = 0;
        char[] result = new char[len]; /* may grow */

        /* Just copy the first few upperCase characters. */
        System.arraycopy(value, 0, result, 0, firstLower);

        String lang = locale.getLanguage();
        boolean localeDependent =
                (lang == "tr" || lang == "az" || lang == "lt");
        char[] upperCharArray;
        int upperChar;
        int srcChar;
        int srcCount;
        for (int i = firstLower; i < len; i += srcCount) {
            srcChar = (int)value[i];
            if ((char)srcChar >= Character.MIN_HIGH_SURROGATE &&
                (char)srcChar <= Character.MAX_HIGH_SURROGATE) {
                srcChar = codePointAt(i);
                srcCount = Character.charCount(srcChar);
            } else {
                srcCount = 1;
            }
            if (localeDependent) {
                upperChar = ConditionalSpecialCasing.toUpperCaseEx(this, i, locale);
            } else {
                upperChar = Character.toUpperCaseEx(srcChar);
            }
            if ((upperChar == Character.ERROR)
                    || (upperChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
                if (upperChar == Character.ERROR) {
                    if (localeDependent) {
                        upperCharArray =
                                ConditionalSpecialCasing.toUpperCaseCharArray(this, i, locale);
                    } else {
                        upperCharArray = Character.toUpperCaseCharArray(srcChar);
                    }
                } else if (srcCount == 2) {
                    resultOffset += Character.toChars(upperChar, result, i + resultOffset) - srcCount;
                    continue;
                } else {
                    upperCharArray = Character.toChars(upperChar);
                }

                /* Grow result if needed */
                int mapLen = upperCharArray.length;
                if (mapLen > srcCount) {
                    char[] result2 = new char[result.length + mapLen - srcCount];
                    System.arraycopy(result, 0, result2, 0, i + resultOffset);
                    result = result2;
                }
                for (int x = 0; x < mapLen; ++x) {
                    result[i + resultOffset + x] = upperCharArray[x];
                }
                resultOffset += (mapLen - srcCount);
            } else {
                result[i + resultOffset] = (char)upperChar;
            }
        }
        return new String(result, 0, len + resultOffset);
    }

    /**
     * Converts all of the characters in this {@code String} to upper
     * case using the rules of the default locale. This method is equivalent to
     * {@code toUpperCase(Locale.getDefault())}.
     * <p>
     * <b>Note:</b> This method is locale sensitive, and may produce unexpected
     * results if used for strings that are intended to be interpreted locale
     * independently.
     * Examples are programming language identifiers, protocol keys, and HTML
     * tags.
     * For instance, {@code "title".toUpperCase()} in a Turkish locale
     * returns {@code "T\u005Cu0130TLE"}, where '\u005Cu0130' is the
     * LATIN CAPITAL LETTER I WITH DOT ABOVE character.
     * To obtain correct results for locale insensitive strings, use
     * {@code toUpperCase(Locale.ROOT)}.
     * <p>
     * @return  the {@code String}, converted to uppercase.
     * @see     java.lang.String#toUpperCase(Locale)
     */
    public String toUpperCase() {
        return toUpperCase(Locale.getDefault());
    }

    /**
     * Returns a string whose value is this string, with any leading and trailing
     * whitespace removed.
     * <p>
     * If this {@code String} object represents an empty character
     * sequence, or the first and last characters of character sequence
     * represented by this {@code String} object both have codes
     * greater than {@code '\u005Cu0020'} (the space character), then a
     * reference to this {@code String} object is returned.
     * <p>
     * Otherwise, if there is no character with a code greater than
     * {@code '\u005Cu0020'} in the string, then a
     * {@code String} object representing an empty string is
     * returned.
     * <p>
     * Otherwise, let <i>k</i> be the index of the first character in the
     * string whose code is greater than {@code '\u005Cu0020'}, and let
     * <i>m</i> be the index of the last character in the string whose code
     * is greater than {@code '\u005Cu0020'}. A {@code String}
     * object is returned, representing the substring of this string that
     * begins with the character at index <i>k</i> and ends with the
     * character at index <i>m</i>-that is, the result of
     * {@code this.substring(k, m + 1)}.
     * <p>
     * This method may be used to trim whitespace (as defined above) from
     * the beginning and end of a string.
     *
     * @return  A string whose value is this string, with any leading and trailing white
     *          space removed, or this string if it has no leading or
     *          trailing white space.
     */
    public String trim() {
        int len = value.length;
        int st = 0;
        char[] val = value;    /* avoid getfield opcode */

        while ((st < len) && (val[st] <= ' ')) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < value.length)) ? substring(st, len) : this;
    }

    /**
     * This object (which is already a string!) is itself returned.
     *
     * @return  the string itself.
     */
    public String toString() {
        return this;
    }

    /**
     * Converts this string to a new character array.
     *
     * @return  a newly allocated character array whose length is the length
     *          of this string and whose contents are initialized to contain
     *          the character sequence represented by this string.
     */
    public char[] toCharArray() {
        // Cannot use Arrays.copyOf because of class initialization order issues
        char result[] = new char[value.length];
        System.arraycopy(value, 0, result, 0, value.length);
        return result;
    }

    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     *
     * <p> The locale always used is the one returned by {@link
     * java.util.Locale#getDefault() Locale.getDefault()}.
     *
     * @param  format
     *         A <a href="../util/Formatter.html#syntax">format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws  java.util.IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification.
     *
     * @return  A formatted string
     *
     * @see  java.util.Formatter
     * @since  1.5
     */
    public static String format(String format, Object... args) {
        return new Formatter().format(format, args).toString();
    }

    /**
     * Returns a formatted string using the specified locale, format string,
     * and arguments.
     *
     * @param  l
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting.  If {@code l} is {@code null} then no localization
     *         is applied.
     *
     * @param  format
     *         A <a href="../util/Formatter.html#syntax">format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the
     *         <a href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws  java.util.IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification
     *
     * @return  A formatted string
     *
     * @see  java.util.Formatter
     * @since  1.5
     */
    public static String format(Locale l, String format, Object... args) {
        return new Formatter(l).format(format, args).toString();
    }

    /**
     * Returns the string representation of the {@code Object} argument.
     *
     * @param   obj   an {@code Object}.
     * @return  if the argument is {@code null}, then a string equal to
     *          {@code "null"}; otherwise, the value of
     *          {@code obj.toString()} is returned.
     * @see     java.lang.Object#toString()
     */
    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }

    /**
     * Returns the string representation of the {@code char} array
     * argument. The contents of the character array are copied; subsequent
     * modification of the character array does not affect the returned
     * string.
     *
     * @param   data     the character array.
     * @return  a {@code String} that contains the characters of the
     *          character array.
     */
    public static String valueOf(char data[]) {
        return new String(data);
    }

    /**
     * Returns the string representation of a specific subarray of the
     * {@code char} array argument.
     * <p>
     * The {@code offset} argument is the index of the first
     * character of the subarray. The {@code count} argument
     * specifies the length of the subarray. The contents of the subarray
     * are copied; subsequent modification of the character array does not
     * affect the returned string.
     *
     * @param   data     the character array.
     * @param   offset   initial offset of the subarray.
     * @param   count    length of the subarray.
     * @return  a {@code String} that contains the characters of the
     *          specified subarray of the character array.
     * @exception IndexOutOfBoundsException if {@code offset} is
     *          negative, or {@code count} is negative, or
     *          {@code offset+count} is larger than
     *          {@code data.length}.
     */
    public static String valueOf(char data[], int offset, int count) {
        return new String(data, offset, count);
    }

    /**
     * Equivalent to {@link #valueOf(char[], int, int)}.
     *
     * @param   data     the character array.
     * @param   offset   initial offset of the subarray.
     * @param   count    length of the subarray.
     * @return  a {@code String} that contains the characters of the
     *          specified subarray of the character array.
     * @exception IndexOutOfBoundsException if {@code offset} is
     *          negative, or {@code count} is negative, or
     *          {@code offset+count} is larger than
     *          {@code data.length}.
     */
    public static String copyValueOf(char data[], int offset, int count) {
        return new String(data, offset, count);
    }

    /**
     * Equivalent to {@link #valueOf(char[])}.
     *
     * @param   data   the character array.
     * @return  a {@code String} that contains the characters of the
     *          character array.
     */
    public static String copyValueOf(char data[]) {
        return new String(data);
    }

    /**
     * Returns the string representation of the {@code boolean} argument.
     *
     * @param   b   a {@code boolean}.
     * @return  if the argument is {@code true}, a string equal to
     *          {@code "true"} is returned; otherwise, a string equal to
     *          {@code "false"} is returned.
     */
    public static String valueOf(boolean b) {
        return b ? "true" : "false";
    }

    /**
     * Returns the string representation of the {@code char}
     * argument.
     *
     * @param   c   a {@code char}.
     * @return  a string of length {@code 1} containing
     *          as its single character the argument {@code c}.
     */
    public static String valueOf(char c) {
        char data[] = {c};
        return new String(data, true);
    }

    /**
     * Returns the string representation of the {@code int} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Integer.toString} method of one argument.
     *
     * @param   i   an {@code int}.
     * @return  a string representation of the {@code int} argument.
     * @see     java.lang.Integer#toString(int, int)
     */
    public static String valueOf(int i) {
        return Integer.toString(i);
    }

    /**
     * Returns the string representation of the {@code long} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Long.toString} method of one argument.
     *
     * @param   l   a {@code long}.
     * @return  a string representation of the {@code long} argument.
     * @see     java.lang.Long#toString(long)
     */
    public static String valueOf(long l) {
        return Long.toString(l);
    }

    /**
     * Returns the string representation of the {@code float} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Float.toString} method of one argument.
     *
     * @param   f   a {@code float}.
     * @return  a string representation of the {@code float} argument.
     * @see     java.lang.Float#toString(float)
     */
    public static String valueOf(float f) {
        return Float.toString(f);
    }

    /**
     * Returns the string representation of the {@code double} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Double.toString} method of one argument.
     *
     * @param   d   a {@code double}.
     * @return  a  string representation of the {@code double} argument.
     * @see     java.lang.Double#toString(double)
     */
    public static String valueOf(double d) {
        return Double.toString(d);
    }

    /**
     * Returns a canonical representation for the string object.
     * <p>
     * A pool of strings, initially empty, is maintained privately by the
     * class {@code String}.
     * <p>
     * When the intern method is invoked, if the pool already contains a
     * string equal to this {@code String} object as determined by
     * the {@link #equals(Object)} method, then the string from the pool is
     * returned. Otherwise, this {@code String} object is added to the
     * pool and a reference to this {@code String} object is returned.
     * <p>
     * It follows that for any two strings {@code s} and {@code t},
     * {@code s.intern() == t.intern()} is {@code true}
     * if and only if {@code s.equals(t)} is {@code true}.
     * <p>
     * All literal strings and string-valued constant expressions are
     * interned. String literals are defined in section 3.10.5 of the
     * <cite>The Java&trade; Language Specification</cite>.
     *
     * @return  a string that has the same contents as this string, but is
     *          guaranteed to be from a pool of unique strings.
     */
    public native String intern();
}
