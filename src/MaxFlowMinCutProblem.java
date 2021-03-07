
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import java.awt.BasicStroke;
import java.awt.Stroke;
import org.apache.commons.collections15.Transformer;

public class MaxFlowMinCutProblem extends javax.swing.JFrame {

    private int[][] graph;
    private int[] parent;
    private Queue<Integer> queue;
    private int numberOfNodes;
    private boolean[] visited;
    private Set<Pair> cutSet;
    private ArrayList<Integer> ulasilir;
    private ArrayList<Integer> ulasilamaz;

    ArrayList<JTextField> textBArray = new ArrayList<>();

    public MaxFlowMinCutProblem(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
        this.queue = new LinkedList<Integer>();
        parent = new int[numberOfNodes + 1];
        visited = new boolean[numberOfNodes + 1];
        cutSet = new HashSet<Pair>();
        ulasilir = new ArrayList<Integer>();
        ulasilamaz = new ArrayList<Integer>();
    }

    public boolean kontrol (int kaynak, int amac, int graph[][])
    {
        boolean pathFound = false;
        int hedef, element;
        for (int dugum = 1; dugum <= numberOfNodes; dugum++)
        {
            parent[dugum] = -1;
            visited[dugum] = false;
        }
        queue.add(kaynak);
        parent[kaynak] = -1;
        visited[kaynak] = true;
 
        while (!queue.isEmpty())
        {
            element = queue.remove();
            hedef = 1;
            while (hedef <= numberOfNodes)
            {
                if (graph[element][hedef] > 0 &&  !visited[hedef])
                {
                    parent[hedef] = element;
                    queue.add(hedef);
                    visited[hedef] = true;
                }
                hedef++;
            }
        }
 
        if (visited[amac])
        {
            pathFound = true;
        }
        return pathFound;
    }

    public int maxFlowMinCut (int graph[][], int kaynak, int hedef)
    {
        int u, v;
        int maxFlow = 0;
        int pathFlow;
        int[][] residualGraph = new int[numberOfNodes + 1][numberOfNodes + 1];
 
        for (int kaynakDugum = 1; kaynakDugum <= numberOfNodes; kaynakDugum++)
        {
            for (int hedefDugum = 1; hedefDugum <= numberOfNodes; hedefDugum++)
            {
                residualGraph[kaynakDugum][hedefDugum] = graph[kaynakDugum][hedefDugum];
            }
        }
        
        
        while (kontrol(kaynak, hedef, residualGraph))
        {
            pathFlow = Integer.MAX_VALUE;
            for (v = hedef; v != kaynak; v = parent[v])
            {
                u = parent[v];
                pathFlow = Math.min(pathFlow,residualGraph[u][v]);
            }
            for (v = hedef; v != kaynak; v = parent[v])
            {
                u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }
            maxFlow += pathFlow;	
        }
        
        for (int dugum = 1; dugum <= numberOfNodes; dugum++)
        {
            if (kontrol(kaynak, dugum, residualGraph))
            {
                ulasilir.add(dugum);
            }
            else
            {
                ulasilamaz.add(dugum);
            }
        }
        
        for (int i = 0; i < ulasilir.size(); i++)
        {
            for (int j = 0; j < ulasilamaz.size(); j++)
            {
                if (graph[ulasilir.get(i)][ulasilamaz.get(j)] > 0)
                {
                    cutSet.add(new Pair(ulasilir.get(i), ulasilamaz.get(j)));
                }
            }
        }
        
        DirectedSparseGraph g = new DirectedSparseGraph();
        DirectedSparseGraph r = new DirectedSparseGraph();
        
        for (int i = 1; i < numberOfNodes; i++) {
            g.addVertex(i);
            r.addVertex(i);
        }
        
        for (int i = 1; i <= numberOfNodes; i++) {
            for (int j = 1; j <= numberOfNodes; j++) {
                if (graph[i][j] > 0 && residualGraph[j][i] > 0 && graph[i][j] != residualGraph[i][j]) {
                    boolean check = false;
                    Iterator<Pair> iterator = cutSet.iterator();
                    while (iterator.hasNext()) {
                        Pair pair = iterator.next();
                        System.out.println(pair.kaynak + "-" + pair.hedef);
                        if (j == pair.hedef && i == pair.kaynak)
                        {
                            check = true;
                            g.addEdge("Cut" + pair.kaynak + "-" + pair.hedef + " " + residualGraph[pair.hedef][pair.kaynak], pair.kaynak, pair.hedef);
                            System.out.println("cut " + residualGraph[pair.hedef][pair.kaynak]);
                        }
                    }
                    if(check == false)
                    {
                        g.addEdge("Edge" + i + "-" + j + " " + residualGraph[j][i], i, j);
                        System.out.println("edge " + residualGraph[j][i]);
                    }
                }
                if (graph[i][j] > 0)
                {
                    r.addEdge("Edge" + i + "-" + j + " " + graph[i][j], i, j);
                }
            }
        }
        
        Layout<Integer, String> layout = new CircleLayout(g);
        layout.setSize(new Dimension(350,350));
        BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<>(layout);
        vv.setPreferredSize(new Dimension(350,350)); 
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

        Layout<Integer, String> layout2 = new CircleLayout(r);
        layout2.setSize(new Dimension(350,350));
        BasicVisualizationServer<Integer,String> vv2 = new BasicVisualizationServer<>(layout2);
        vv2.setPreferredSize(new Dimension(350,350)); 
        vv2.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv2.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv2.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

        
        JFrame frame = new JFrame();
        frame.setTitle("Max Flow & Min Cut");
        frame.getContentPane().add(vv);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
        JFrame frame2 = new JFrame();
        frame2.setTitle("Orijinal Graph");
        frame2.getContentPane().add(vv2);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.pack();
        frame2.setVisible(true);
        
        return maxFlow;
    }

    public MaxFlowMinCutProblem() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Node Sayısı");

        jTextField1.setName("nodeNum"); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setText("MaxFlowMinCut Hesapla");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Giriş Node");

        jLabel3.setText("Çıkış Node");

        jButton2.setText("Gönder");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 1, Short.MAX_VALUE))
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int maxFlow;
        int nodeNumber = Integer.parseInt(jTextField1.getText());
        int input = Integer.parseInt(jTextField2.getText());
        int output = Integer.parseInt(jTextField3.getText());

        MaxFlowMinCutProblem maxFlowMinCut = new MaxFlowMinCutProblem(nodeNumber);
        maxFlow = maxFlowMinCut.maxFlowMinCut(graph, input, output);
        System.out.println("The Max Flow is " + maxFlow);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        JFrame f = new JFrame();
        JPanel p = new JPanel();
        int nodeNumber = Integer.parseInt(jTextField1.getText());

        graph = new int[nodeNumber + 1][nodeNumber + 1];

        f.setSize((nodeNumber) * 80, (nodeNumber) * 50);
        for (int i = 0; i < nodeNumber * nodeNumber; i++) {
            JTextField textField = new JTextField(5);
            textField.setText("0");
            textBArray.add(textField);
            p.add(textField);
            f.add(p);
            f.setVisible(true);
        }

        JButton button = new JButton();
        button.setPreferredSize(new Dimension(67, 23));
        button.setText("Ekle");
        p.add(button);
        f.add(p);
        f.setVisible(true);

        button.addActionListener((ActionEvent ae) -> {
            int arrayIndex = 0;
            for (int i = 1; i <= nodeNumber; i++) {
                for (int j = 1; j <= nodeNumber; j++) {
                    graph[i][j] = Integer.parseInt(textBArray.get(arrayIndex).getText());
                    System.out.print(" - " + graph[i][j] + " - ");
                    arrayIndex++;
                }
                System.out.println("");
            }
            f.setVisible(false);
        });
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MaxFlowMinCutProblem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MaxFlowMinCutProblem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MaxFlowMinCutProblem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MaxFlowMinCutProblem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MaxFlowMinCutProblem().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}

class Pair {

    public int kaynak;
    public int hedef;

    public Pair(int kaynak, int hedef) {
        this.kaynak = kaynak;
        this.hedef = hedef;
    }

    public Pair() {

    }
}
