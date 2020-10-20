package handle;

import analyze.analyicPacket;
import entity.BasePacket;
import entity.InfoNode;
import utils.PackInfo;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Vector;

public class PacketHandle implements Handler {

    //存储数据包的信息
    private InfoNode node=null;
    //存储JTree
    private Vector<JTree> trees=new Vector<>();
    public PacketHandle(){}


    public Vector<JTree> getTrees() {
        return trees;
    }

    public InfoNode getNode() {
        return node;
    }

    @Override
    public void handlePacket(BasePacket packet) {
        this.node= analyicPacket.analyze(packet);

        //createTree(root);
    }

    private void createTree(InfoNode node){
        DefaultMutableTreeNode rootNode=new DefaultMutableTreeNode(node.getMessage());
        if(node.hasChildNodes()){
            for (InfoNode child:node.getChildNodes()){
                rootNode.add(new DefaultMutableTreeNode(child.getMessage()));
                createTree(child);
            }
            JTree mytree=new JTree(rootNode);
            // 设置树显示根节点句柄
            mytree.setShowsRootHandles(true);
            // 设置节点选中监听器
            mytree.addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    if(mytree.isExpanded(e.getPath())){
                        mytree.collapsePath(e.getPath());
                    }else {
                        mytree.expandPath(e.getPath());
                    }
                    System.out.println("当前被选中的节点: " + e.getPath());
                }
            });

            trees.add(mytree);
        }
        if(node.hasNext()){
            createTree(node.getNext());
        }
    }
}
