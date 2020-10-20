package entity;

import java.util.Vector;

    //存储节点信息
    public  class InfoNode{
        //信息节点的内容
        private String message="";
        //信息节点的子节点
        private Vector<InfoNode> childNodes=new Vector<>();
        //存放下一层协议的信息
        private InfoNode next=null;

        public InfoNode() {
        }
        public InfoNode(String message){
            this.message=message;
        }
        public InfoNode(String message, Vector<InfoNode> childNodes) {

            this.message = message;
            this.childNodes = childNodes;
        }


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Vector<InfoNode> getChildNodes() {
            return childNodes;
        }

        public void setChildNodes(Vector<InfoNode> childNodes) {
            this.childNodes = childNodes;
        }

    public InfoNode getNext() {
        return next;
    }

    public void setNext(InfoNode next) {
        this.next = next;
    }
    public boolean hasNext(){
            if(this.next==null){
                return false;
            }
            return true;
    }
    public boolean hasChildNodes(){
            if(childNodes==null||childNodes.size()==0){
                return false;
            }
            return true;
        }
        public int length(){
           if(childNodes==null){
               throw new NullPointerException();
           }else {
               return childNodes.size();
           }
        }
        public InfoNode getItem(int index){
            if(index<0||index>=childNodes.size()){
                throw new IndexOutOfBoundsException();
            }else {
                return childNodes.get(index);
            }
        }
        public void addItem(InfoNode node){
            this.childNodes.add(node);
        }
        public void setItem(int index,InfoNode node){
            childNodes.set(index,node);
        }
        public void deleteItem(int index){
            childNodes.remove(index);
        }

        private String getInfo(InfoNode node,int count){
            StringBuilder msg=new StringBuilder(node.getMessage());
            StringBuilder addStr=new StringBuilder("\n");
            for(int i=0;i<=count;i++){
                addStr.append("\t");
            }
            if(node.hasChildNodes()){
                for (InfoNode child:node.getChildNodes()){
                    msg.append(addStr);
                   msg.append(getInfo(child,count+1));
                }
            }
            if(node.hasNext()){
                msg.append("\n");
                msg=msg.append(getInfo(node.next,0));
            }
            return msg.toString();
        }

        public String toString(){
            return getInfo(this,0)+"\n";
        }
    }
