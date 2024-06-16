# [allmethods.h]()
## 定义了一个用于Android平台的多媒体播放器中的解复用器（Demuxer）类。
#### dexumuer(inputpath)
- 构造函数只需传入视频的地址。
- 私有成员变量包括多媒体格式上下文、编解码器上下文、编解码器、文件路径、互斥锁和解码完成标志等。
- 公有成员变量和构造函数用于初始化解复用器，设置文件路径，并查找视频和音频流。
#### find_streams
- 成员函数用于查找多媒体文件中的视频和音频流，并初始化解码器。
#### [decode_video_thead和decode_audio_thread]()
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
#### [ThreadPool(size_t num_threads)]() 
- 创建指定数量的线程，并将它们放入一个线程池中。


