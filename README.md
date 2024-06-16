# [allmethods.h]()
## 定义了一个用于Android平台的多媒体播放器中的解复用器（Demuxer）类。
#### [声明]（）
- 构造函数只需传入视频的地址。
- 私有成员变量包括多媒体格式上下文、编解码器上下文、编解码器、文件路径、互斥锁和解码完成标志等。
- 公有成员变量和构造函数用于初始化解复用器，设置文件路径，并查找视频和音频流。
#### [find_streams]()
- 成员函数用于查找多媒体文件中的视频和音频流，并初始化解码器。
#### [decode_video_thead和decode_audio_thread]()
- 这两个方法用来解码视频和解码音频。
- av_read_frame() 读取数据包，然后通过解码器解码这些数据包，并将解码后的帧放入队列中。
#### [~Demuxer()]()
- 析构函数，释放资源。
## [Queue.h]()
- 封装了模板队列,处理任何格式的数据。
- std::vector<T> elements; 用于存储队列中的元素。
- mutable std::mutex mtx; 定义了一个可变的互斥锁，用于同步访问共享资源。
- std::condition_variable cv; 定义了一个条件变量，用于线程间的协调。
- size_t max_size; 定义了队列的最大容量。
#### [Queue(size_t size = 1000) : max_size(size) {}]
- 是类的构造函数，它接受一个可选参数 size 来设置队列的最大容量，默认为1000。
#### [void enqueue(const T& element)] 
- 是向队列添加元素的成员函数。它使用 std::unique_lock 来锁定互斥锁，并等待队列未满的条件。如果队列已满，调用线程将被阻塞，直到条件变量 cv 被唤醒。添加元素后，它将通知一个等待的消费者。
- T dequeue() 是从队列中移除元素的成员函数。它同样使用 std::unique_lock 锁定互斥锁，并等待队列非空的条件。如果队列为空，调用线程将被阻塞。当条件满足时，它将移除并返回队列的第一个元素。
- T getFrameByIndex(size_t index) 是通过索引获取队列中特定位置元素的成员函数。它使用 std::lock_guard 来锁定互斥锁，然后检查索引是否有效，如果索引超出范围，则抛出异常。
- bool isEmpty() const 是检查队列是否为空的成员函数。这里注释掉了 std::lock_guard，但实际上为了线程安全，应该使用它来锁定互斥锁。
- size_t getLen() 是获取队列长度的成员函数。同样，这里注释掉了 std::lock_guard，但出于线程安全考虑，应该使用它。
