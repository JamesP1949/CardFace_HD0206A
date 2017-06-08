# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\studioSdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化  不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#记录生成的日志数据,gradle build时在本项目根目录输出#
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
#记录生成的日志数据，gradle build时 在本项目根目录输出-end#
#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.os.IInterface

#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

#support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

# support-v7-cardview
-keep class android.support.v7.widget.RoundRectDrawable { *; }

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclasseswithmembernames class * { # 保持 native 方法不被混淆
 native <methods>;
}

-keepclasseswithmembers class * { # 保持自定义控件类不被混淆
 public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
 public void *(android.view.View);
}

-keepclassmembers enum * { # 保持枚举 enum 类不被混淆
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
 public static final android.os.Parcelable$Creator *;
}
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}
#保护注解
-keepattributes *Annotation*

#避免混淆泛型 如果混淆报错建议关掉
-keepattributes Signature

#-keep class com.nineoldandroids.** { *; }
#-keep interface com.nineoldandroids.** { *; }
#-dontwarn com.nineoldandroids.**

# 删除日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}


#解决：can't find referenced class java.beans.Beans
-dontwarn org.apache.commons.collections.BeanMap
-dontwarn org.apache.commons.beanutils.**
-dontwarn java.beans.**

#######################     Library        ###################################
# 高级的gradle插件打包混淆时自动做了jar文件的处理
-keep class cn.com.aratek.** { *; }

#######################     常用第三方模块的混淆选项         ###################################

#butterknife7.x
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#butterknife8.4.1
#-keep public class * implements butterknife.Unbinder { public <init>(...); }
#-keep class butterknife.*
#-keepclasseswithmembernames class * { @butterknife.* <methods>; }
#-keepclasseswithmembernames class * { @butterknife.* <fields>; }

#greenDAO 数据库
### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**


#Dagger2.x
#-keep class dagger.* { *; }
#-keep class javax.inject.* { *; }
#-keepclassmembers,allowobfuscation class * {
#    @javax.inject.* *;
#    @dagger.* *;
#    <init>();
#}


#opencsv
-keep class com.opencsv.CSVWriter

#easyrecyclerview
-keep class com.jude.easyrecyclerview.** { *; }

#解决duplicate definition of library class
#-dontnote org.apache.http.conn.scheme.**
#-dontnote org.apache.http.conn.ConnectTimeoutException
#-dontnote org.apache.http.params.**
#-dontnote android.net.http.**

