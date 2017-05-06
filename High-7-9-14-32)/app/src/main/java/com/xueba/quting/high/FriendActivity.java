package com.xueba.quting.high;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wanglei.qq.bean.Friends;
import com.example.wanglei.qq.bean.QQinfor;
import com.example.wanglei.qq.bean.friendsobject;
import com.xueba.quting.high.Thread.SendGet;
import com.xueba.quting.high.other.MulDownloader;

import java.util.ArrayList;
import java.util.HashMap;


public class FriendActivity extends Activity {
    protected ListView chatListView=null;
    protected MyAdapter adapter=null;
    ArrayList<HashMap<String,Object>> list1=null;
    HashMap<String,Object> map=null;
    private QQinfor qq_friendinfor=null;//���ܷ�������������
    private QQinfor chat_qqinfrom=null;
    String username=null;
    Bitmap chat_we=null;
    ArrayList<friendsobject> storelist;
    ArrayList<friendsobject> pulllist;
    ImageView me;
    ImageView ivMyFriendBack;


    private Handler rHandler = new Handler() {
        //������Ϣ�����е���Ϣ
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==2)
            {
                /******�������������Ķ���***********/
                qq_friendinfor=(QQinfor)msg.obj;
                System.out.print("GetQQinfor--->"+ qq_friendinfor);
                friendsobject temp;
                for(int i=0;i<qq_friendinfor.getFrineds_list().size();i++){
                    temp=new friendsobject();
                    temp.setUid(qq_friendinfor.getFrineds_list().get(i).getUid());
                    temp.setName(qq_friendinfor.getFrineds_list().get(i).getName());
                    temp.setChara(qq_friendinfor.getFrineds_list().get(i).getChara());
                    temp.setUrl(qq_friendinfor.getFrineds_list().get(i).getLogourl());
                    storelist.add(temp);
                }
                for(int i=0;i<qq_friendinfor.getFrineds_list().size();i++){
                    new MulDownloader(storelist.get(i),rHandler).start();
                }

            }

            if(msg.what==3){
                pulllist.add((friendsobject)msg.obj);
                if(pulllist.size()==qq_friendinfor.getFrineds_list().size()){
                    showlist();
                }
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        //ÿһ�е�����/Map�ļ���  �����Ӧ��View�ӿؼ���ID
        // String[] from={"userPhoto","userName","userSign"};           //��������ݶ�Ӧ����HashMap�еļ�
        // int[] to={R.id.ct_photo,R.id.ct_name,R.id.ct_sign};
        list1=new ArrayList<HashMap<String,Object>>();
        storelist=new ArrayList<friendsobject>();
        pulllist=new ArrayList<friendsobject>();
        chatListView=(ListView)findViewById(R.id.qq_list);
        me=(ImageView)findViewById(R.id.imageme);
        ivMyFriendBack=(ImageView)findViewById(R.id.ivfriendBack);
        ivMyFriendBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent=getIntent();
        //��ȡ���ݹ�����QQinfor
        chat_qqinfrom=(QQinfor)intent.getSerializableExtra("QQinfor");//�����ϸ�������
        chat_we=(Bitmap)intent.getParcelableExtra("Ubitmap");//����û�ͷ��
       // me.setImageBitmap(chat_we);
        username=chat_qqinfrom.getUid();
        initialize();


    }

    private  void initialize(){
        QQinfor QQ_getfriend=new QQinfor();
        QQ_getfriend.setpid(401);//��ȡ����ҳ��
        QQ_getfriend.setUid(username);
        new SendGet( QQ_getfriend, rHandler, 401).start();
    }
    //////չʾ�����б�////
    private void showlist(){

        //�����Զ����MyAdapter����
        String[] from={"userPhoto","userName","userSign"};           //��������ݶ�Ӧ����HashMap�еļ�
        int[] to={R.id.ct_photo,R.id.ct_name,R.id.ct_sign};
        for(int i=0; i<qq_friendinfor.getFrineds_list().size(); i++){
            map=new HashMap<String,Object>();    //map����put������Ӽ�ֵ��
            map.put("userPhoto",pulllist.get(i).getBitmap());//ӳ��ͼƬ
            map.put("userName", pulllist.get(i).getName());
            map.put("userSign",pulllist.get(i).getChara());
            list1.add(map);
        }
        adapter=new MyAdapter(this,R.layout.list_item,list1,from,to);
        //����ListView��setAdapter()��������������
        chatListView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    ///MyAdapter��
    public class MyAdapter extends BaseAdapter{
        private Context context=null;
        private int resources;
        private ArrayList<HashMap<String,Object>> list=null;
        private String[] from;
        private int[] to;


        public MyAdapter(Context context, int resources,
                         ArrayList<HashMap<String, Object>> list, String[] from, int[] to) {
            super();
            this.context = context;
            this.resources = resources;
            this.list = list;
            this.from = from;
            this.to = to;
        }

        /**
         * ʣ�µ������������ʵ��BaseAdapter���⼸���෽���Ϳ�����
         */

        @Override
        public int getCount() {        //����������ص���ListView������
            // TODO Auto-generated method stub
            return list.size();
        }



        @Override
        public Object getItem(int arg0) {      //�������û��Ҫʹ�ã�������getItemId����
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int itemId) {     //���ĳһ��ʱ����ø÷��������β��ɰ�׿ϵͳ�ṩ
            // TODO Auto-generated method stub
            return itemId;
        }

        /**
         * getView����Ϊϵͳ�ڻ���ÿһ��ʱ���ã��ڴ˷�����Ҫ������Ҫ��ʾ�����֣�ͼƬ��
         * �Լ�Ϊ��ť���ü�������
         *
         * �β����壺
         * position����ǰ���Ƶ�item ��λ�ã�ID����
         * convertView��ϵͳ�ڻ���ListViewʱ������ǻ��Ƶ�һ��Item������һ�У���convertViewΪnull,��
         * ���Ƶڶ������Ժ��Item��convertView��Ϊ�գ���ʱ����ֱ���������convertView��getTag()��������ø��ؼ�
         * ��ʵ�������Ӧ�����ã�������Լӿ��ͼ�ٶȡ�
         *
         * Ϊ��ΪconvertView���ø�����ϢTag�����ﴴ��һ���ڲ���ViewHolder������ʢ��һ�������пؼ������ã�����Щ����
         * ʵ�����ΪconvertView�ĸ�����Ϣ��
         */
        class ViewHolder{
            public ImageButton ctPhoto=null;
            public TextView ctName=null,ctSign=null;

            /*
             * ��������Կ�����from��to����˴�֮���Ԫ��Ӧ��һһ��Ӧ��ͬʱfrom��to����Ԫ���ڲ���˳��ͬ�����ListView
             * ���ֵ�λ��Ҳ�᲻ͬ��
             */
            public ViewHolder(View convertView){
                ctPhoto=(ImageButton)convertView.findViewById(to[0]);
            /*ע��View��Activity�����������࣬����Ҫ���ò����ļ����ڲ��������ӿؼ����Ҷ���findViewById()
             * ����֮��û�����Եļ̳й�ϵ
             */
                ctName=(TextView)convertView.findViewById(to[1]);
                ctSign=(TextView)convertView.findViewById(to[2]);

            }

        }

        class ImageListener implements OnClickListener{

            private int position;

            public ImageListener(int position){
                this.position=position;
            }                          //���캯��û�з���ֵ

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent=new Intent();
                QQinfor chatobject=new QQinfor();
                chatobject.setUid(username);
                ArrayList<Friends> temp_list=new ArrayList<Friends>();
                Friends temp=new Friends();
                temp.setUid(pulllist.get(position).getUid());
                temp_list.add(temp);
                chatobject.setFrineds_list(temp_list);
                Bitmap chat_other=null;
                chat_other=pulllist.get(position).getBitmap();
                intent.putExtra("QQinfor", chatobject);//���ݶ���
                intent.putExtra("Obitmap",chat_other);//�����������ͼ��
                intent.putExtra("Ubitmap",chat_we);//�����û��Լ�ͷ��
                intent.putExtra("Chatname", pulllist.get(position).getName());//��¼����
                intent.setClass(FriendActivity.this, ChattwoActivity.class);//��ת
                startActivity(intent);

            }

        }


        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub

            /**
             * �����ж��ǲ��ǵ�һ�δ���Item�����ǣ��򴴽�convertViewʵ���ViewHolder���󣬲�ͨ��fandViewById()����
             * ���ÿһ�������пռ��ʵ�����ViewHolder�����У�Ȼ���convertView���ñ�ǩ
             */
            ViewHolder viewHolder=null;

            //ע��convertView�������ⴴ���ģ���Ҫ��LayoutInflater,���list_item�����ļ�����
            if(convertView==null){
                LayoutInflater inflater=LayoutInflater.from(context);
                convertView=inflater.inflate(resources,null);    //�����null��һ��ViewGroup�βΣ����ò���
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            }
            else{
                viewHolder=(ViewHolder)convertView.getTag();    //ͨ��getTag()������ø�����Ϣ
            }
            /**
             * �����viewHolder�еĸ����ؼ�������Ӧ������
             */
            /**
             * @author DragonGN
             * ���������һ�����⣺�ڻ��Ƶ�ǰ�е�ListItemʱ��ֻ��Ҫ�Ե�ǰ�еĿؼ��������ã�������ﲻ�ܼ�һ��for
             * ѭ����ÿһ��list�е�ÿһ��Ԫ�ؽ��б����Ӧ�ø�ݵ�ǰ������ListItem�е�position,Ȼ��
             * ������ݿ�list����Ӧλ�õ�Map����ݣ����пؼ������ã�
             */

            /**
             * ע�����������setBackgroundDrawable() ����setBackground(),���߻ᱨ�?����ǰ�߹����˵�һ�����
             */
            // viewHolder.ctPhoto.setBackgroundDrawable((Drawable)(list.get(position).get(from[0])));
            viewHolder.ctPhoto.setImageBitmap((Bitmap)list.get(position).get(from[0]));
            //Map��Ҫ���һ��Drawable����,�����from��to�е�Ԫ��Ӧ��һһ��Ӧ,��˳��ҲӦ�ö�ӦViewHolder���췽���пؼ��ĵ��õ�˳��
            viewHolder.ctName.setText((String)(list.get(position).get(from[1])));
            viewHolder.ctSign.setText((String) (list.get(position).get(from[2])));
            viewHolder.ctPhoto.setOnClickListener(new ImageListener(position));
            viewHolder.ctName.setOnClickListener(new ImageListener(position));
            viewHolder.ctSign.setOnClickListener(new ImageListener(position));

            return convertView;     //�����ÿһ�е�View���󷵻�
        }

    }

}
