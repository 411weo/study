# [allmethods.h]()
## 定义了一个用于Android平台的多媒体播放器中的解复用器（Demuxer）类。
#### dexumuer(inputpath)
- 构造函数只需传入视频的地址。
- 私有成员变量包括多媒体格式上下文、编解码器上下文、编解码器、文件路径、互斥锁和解码完成标志等。
- 公有成员变量和构造函数用于初始化解复用器，设置文件路径，并查找视频和音频流。
- duration对象存储了视频的长度。
#### find_streams
- 成员函数用于查找多媒体文件中的视频和音频流，并初始化解码器。
#### decode_video_thead和decode_audio_thread
- 这两个方法用来解码视频和解码音频。
- av_read_frame() 读取数据包，然后通过解码器解码这些数据包，并将解码后的帧放入队列中。
#### ~Demuxer()
- 析构函数，释放资源。
## [Queue.h]()
- 封装了模板队列,处理任何格式的数据。
- std::vector<T> elements; 用于存储队列中的元素。
- mutable std::mutex mtx; 定义了一个可变的互斥锁，用于同步访问共享资源。
- std::condition_variable cv; 定义了一个条件变量，用于线程间的协调。
- size_t max_size; 定义了队列的最大容量。
#### Queue(size_t size = 1000) : max_size(size) {}
- 是类的构造函数，它接受一个可选参数 size 来设置队列的最大容量，默认为1000。
#### void enqueue(const T& element)
- 是向队列添加元素的成员函数。它使用 std::unique_lock 来锁定互斥锁，并等待队列未满的条件。如果队列已满，调用线程将被阻塞，直到条件变量 cv 被唤醒。添加元素后，它将通知一个等待的消费者。
- T dequeue() 是从队列中移除元素的成员函数。它同样使用 std::unique_lock 锁定互斥锁，并等待队列非空的条件。如果队列为空，调用线程将被阻塞。当条件满足时，它将移除并返回队列的第一个元素。
- T getFrameByIndex(size_t index) 是通过索引获取队列中特定位置元素的成员函数。它使用 std::lock_guard 来锁定互斥锁，然后检查索引是否有效，如果索引超出范围，则抛出异常。
- bool isEmpty() const 是检查队列是否为空的成员函数。
- size_t getLen() 是获取队列长度的成员函数。
## [CircularBuffer.h]()
- 环形缓冲区模板类。
- 私有成员变量：
- buffer：一个 std::vector<T> 类型的向量，用于存储缓冲区的元素。
- head：一个 size_t 类型的变量，指向队列头部的索引。
- tail：一个 size_t 类型的变量，指向队列尾部的索引，即下一个入队元素的位置。
- count：一个 size_t 类型的变量，表示当前队列中的元素数量。
- capacity：一个 size_t 类型的变量，表示缓冲区的最大容量。
- mtx：一个 std::mutex 类型的互斥锁，用于同步线程对缓冲区的访问。
#### CircularBuffer
- 初始化缓冲区的大小，并设置头、尾、计数器和容量。
#### push(T value)
- 向缓冲区添加一个元素。如果缓冲区已满，调用者将被阻塞，直到缓冲区有空间。
#### pop()
- 从缓冲区移除一个元素。如果缓冲区为空，调用者将被阻塞，直到缓冲区有元素
- not_full 和 not_empty：两个 条件变量，分别用于等待缓冲区非满和非空的状态。
## ThreadPool
- 线程池类
#### ThreadPool(size_t num_threads)
- 构造函数创建指定数量的线程，并将它们放入一个线程池中。
####  ~ThreadPool() 
- 线程池销毁，析构函数

#### std::queue<std::function<void()>>
- 任务队列，存储待执行的任务，每个任务被封装为 std::function。
- 同步机制：使用 std::mutex 和 std::condition_variable 来同步对任务队列的访问和线程间的通信。
#### worker_thread()
- 线程工作循环，函数是每个线程执行的循环，它从任务队列中取出任务并执行。
#### enqueue() 
- 任务提交，函数允许用户提交任务到线程池。
## [Utils]()
####convertYuv420pToRgba(const AVFrame *yuvFrame, uint8_t **outData, int width2, int height2)
- 函数接受一个指向AVFrame结构的指针yuvFrame，这个结构包含了YUV420P格式的图像数据；一个指向uint8_t指针数组的指针outData，用于存储转换后的RGBA数据width2和height2分别是图像的宽度和高度。*outData = (uint8_t *)av_mallocz(width2 * height2 * 4);：使用av_mallocz函数分配一个足够大的缓冲区来存储转换后的RGBA数据。这里4是因为RGBA格式每个像素占用4个字节（红、绿、蓝、透明度）。
struct SwsContext *sws_ctx = sws_getContext(...);：创建一个转换上下文SwsContext，用于在后续步骤中进行图像格式转换。sws_getContext函数初始化转换器，需要指定源图像和目标图像的宽度、高度、像素格式等参数。
if (!sws_ctx) {...}：如果sws_getContext函数失败（即返回NULL），则释放之前分配的内存，并返回错误代码-1。
uint8_t *srcData[3] = {...}; 和 int srcStride[3] = {...};：定义源数据指针数组srcData和步长数组srcStride，分别存储YUV420P格式的三个颜色平面的数据指针和每行数据的字节数。
uint8_t *dstData[1] = {...}; 和 int dstStride[1] = {...};：定义目标数据指针数组dstData和步长数组dstStride，用于存储转换后的RGBA数据。
if (sws_scale(...) < 0) {...}：使用sws_scale函数执行实际的图像格式转换。如果转换失败（即返回值小于0），则释放转换上下文和之前分配的内存，并返回错误代码-1。
sws_freeContext(sws_ctx);：释放转换上下文，释放之前为转换器分配的资源。
## [Native-lib.cpp]()
- 主逻辑函数，在这里声明全局变量，构造对象。
#### 全局变量
- times int类型，当调用setSpeed函数时，改变times的值，控制倍速。
- Queue<AVFrame*> Vframes;//视频缓冲区
- Queue<AVFrame*> Aframes;//音频缓冲区
- index int 类型 表示当前应播放的帧数。
- maplength int 类型 表示已解码的视频帧数
- duration 视频的总长度，从demuxer类中获取。
- videoFramerate 视频帧率，和倍速一起控制渲染速度。
- mtx和condition_variable 生命的锁和条件变量，控制对共享变量的访问
- std::unordered_map<int, uint8_t*> rgbaDataMap;//哈希表，存放rgba视频帧，由于内存是分页式管理，这样会太占内存，开始想弄哈希数组的。
#### toRgba
- 渲染视频的函数，从队列中读取帧，通过convertYuv420pToRgba将frame转换成rgba数据
- 释放资源，maplength
#### getData
- 从哈希表中获取rgba数据
#### play_video
- 播放视频函数，设置宽度和高度，当index小于maplength时，从哈希表中通过index获取rgbadata的地址，然后渲染，渲染一帧后，根据倍速，计算应等待的时间。
#### playaudio
- 播放音频函数
#### nativePlay
- 播放调用的主函数，在该函数中声明demuxer对象，初始化对象，启动解码视频线程，启动解码音频函数，启动解码成rgba线程，启动播放视频线程，启动播放音频线程。

# 项目总结
## 已完成的任务

- 视频能够正常播放，暂停复播，倍速播放，拖拽进度条，返回视频时长。
- 程序应该不会闪退。
- 音频播放只实现了一次。
- 多线程编解码播放。
## 遇到的问题
#### 第一天配环境卡在了合并包那里，耽误了好久，导致最后功能没实现完全，reademe也没认真写。
#### 第二天存储在队列的帧拿出来不对，原因是没有深拷贝放入队列，但第二天是用多线程解决音视频的解码的。a
#### 第三天音频回调不会使用。
#### Makelists里把所有.cpp文件都加入，导致重复链接，只需放入主函数native-lib.cpp就好。
#### 解码音频流时只声明一个对象，复用一个对象对音频和视频编解码失败，解决方案：声明两个demux对象，分别解码。
#### 线程之间同步问题，对共享变量的访问。
#### 最开始没写线程池，在函数里添加线程，然后join，导致该函数阻塞，不能执行其他函数，最后采用线程池，然线程池管理线程的生命周期。
## 能够优化的方向
#### 目前渲染的所有东西都放在内存里，对内存占用太大。
#### 拖拽进度条是通过村内存读取实现的，正确的方法应该是从新解码，然后获取进度条对应的帧的索引，重新从该帧解码。
#### 线程间的通信实现得不好，程序健壮性不够。




