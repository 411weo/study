//堆 的写法
PriorityQueue<int []> pq = new PriorityQueue<>(new Comparator<int []>(){
    @Override
    public int compare(int[] a,int [] b){
        return a[0] - b[0];
    }
});
