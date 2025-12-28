import java.io.*;
import java.util.*;

public class Main {
    static final int INF = Integer.MAX_VALUE / 2;
    static int n, m;
    static List<Edge>[] graph;
    
    static class Edge {
        int to, weight, id;
        Edge(int to, int weight, int id) {
            this.to = to;
            this.weight = weight;
            this.id = id;
        }
    }
    
    // 双向Dijkstra
    static int bidirectionalDijkstra(int start, int end, int forbiddenEdgeId) {
        if (start == end) return 0;
        
        int[] distFromStart = new int[n + 1];
        int[] distFromEnd = new int[n + 1];
        boolean[] visitedFromStart = new boolean[n + 1];
        boolean[] visitedFromEnd = new boolean[n + 1];
        
        Arrays.fill(distFromStart, INF);
        Arrays.fill(distFromEnd, INF);
        distFromStart[start] = 0;
        distFromEnd[end] = 0;
        
        PriorityQueue<int[]> pqStart = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        PriorityQueue<int[]> pqEnd = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        
        pqStart.add(new int[]{start, 0});
        pqEnd.add(new int[]{end, 0});
        
        int best = INF;
        
        while (!pqStart.isEmpty() && !pqEnd.isEmpty()) {
            // 从起点扩展
            if (!pqStart.isEmpty()) {
                int[] current = pqStart.poll();
                int u = current[0];
                
                if (visitedFromStart[u]) continue;
                visitedFromStart[u] = true;
                
                // 检查是否与终点集合相遇
                if (visitedFromEnd[u]) {
                    best = Math.min(best, distFromStart[u] + distFromEnd[u]);
                }
                
                for (Edge edge : graph[u]) {
                    if (edge.id == forbiddenEdgeId) continue;
                    int v = edge.to;
                    int newDist = distFromStart[u] + edge.weight;
                    
                    if (newDist < distFromStart[v]) {
                        distFromStart[v] = newDist;
                        pqStart.add(new int[]{v, newDist});
                    }
                }
            }
            
            // 从终点扩展
            if (!pqEnd.isEmpty()) {
                int[] current = pqEnd.poll();
                int u = current[0];
                
                if (visitedFromEnd[u]) continue;
                visitedFromEnd[u] = true;
                
                // 检查是否与起点集合相遇
                if (visitedFromStart[u]) {
                    best = Math.min(best, distFromStart[u] + distFromEnd[u]);
                }
                
                for (Edge edge : graph[u]) {
                    if (edge.id == forbiddenEdgeId) continue;
                    int v = edge.to;
                    int newDist = distFromEnd[u] + edge.weight;
                    
                    if (newDist < distFromEnd[v]) {
                        distFromEnd[v] = newDist;
                        pqEnd.add(new int[]{v, newDist});
                    }
                }
            }
            
            // 如果当前最优值小于两个队列的最小值，可以提前结束
            if (best < INF) {
                int minStart = pqStart.isEmpty() ? INF : pqStart.peek()[1];
                int minEnd = pqEnd.isEmpty() ? INF : pqEnd.peek()[1];
                if (best <= minStart + minEnd) {
                    break;
                }
            }
        }
        
        return best;
    }
    
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        
        graph = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++) {
            graph[i] = new ArrayList<>();
        }
        
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int a = Integer.parseInt(st.nextToken());
            int b = Integer.parseInt(st.nextToken());
            int v = Integer.parseInt(st.nextToken());
            
            graph[a].add(new Edge(b, v, i));
            graph[b].add(new Edge(a, v, i));
        }
        
        // 首先找到原始最短路径及其边
        int originalDist = bidirectionalDijkstra(n, 1, -1);
        
        // 为了找到路径上的边，我们需要运行一次Dijkstra并记录前驱
        int[] dist = new int[n + 1];
        int[] prevNode = new int[n + 1];
        int[] prevEdgeId = new int[n + 1];
        Arrays.fill(dist, INF);
        Arrays.fill(prevNode, -1);
        Arrays.fill(prevEdgeId, -1);
        dist[n] = 0;
        
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{n, 0});
        
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int d = current[1];
            
            if (d > dist[u]) continue;
            if (u == 1) break;
            
            for (Edge edge : graph[u]) {
                int v = edge.to;
                int newDist = d + edge.weight;
                
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    prevNode[v] = u;
                    prevEdgeId[v] = edge.id;
                    pq.add(new int[]{v, newDist});
                }
            }
        }
        
        // 收集最短路径上的边ID
        List<Integer> pathEdges = new ArrayList<>();
        int current = 1;
        while (prevNode[current] != -1) {
            pathEdges.add(prevEdgeId[current]);
            current = prevNode[current];
        }
        
        // 尝试删除每条边
        int maxDist = originalDist;
        for (int edgeId : pathEdges) {
            int newDist = bidirectionalDijkstra(n, 1, edgeId);
            maxDist = Math.max(maxDist, newDist);
        }
        
        System.out.println(maxDist);
    }
}