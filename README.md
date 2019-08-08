

### 前言
都说研究spring源码会很有收获，它的设计思想以及它核心的抽象都非常值得我们去学习，可是尝试看源码看的头晕，看过一些大佬们造出来简易版的spring,那是一个羡慕啊

所以我也尝试做了一个具备IOC功能MySpringIOC，过程很痛苦，但不得不说，收获不是一般大，实现过程中我体会到了设计模式，面向对象设计原则，重构代码能力等的重要性，以及领悟到了为何人们常说spring接口设计的粒度非常细致。

如果你想研究spring源码，探究Spring的设计思想，不如先从MySpringIOC开始吧!!!

**参考资料:**<<spring揭秘>>、网上的技术文章等

### MySpringIOC具备功能

###基于xml配置文件

1.支持xml中配置<property name="xxx"  value="x"/>完成依赖注入

2.支持构造器注入
		<constructor-arg  ref="accountDao"/>
		<constructor-arg  ref="itemDao"/>	

### 类图
以下是主要的类图
![1559824632099](https://github.com/sunnyColten/MySpringIOC/blob/master/image/main.png)

