package com.xueba.quting.high;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import java.util.*;
import com.xueba.quting.high.Thread.SendGet;
import com.example.wanglei.qq.bean.QQinfor;
import com.example.wanglei.qq.bean.Messageinfor;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ChattwoActivity extends Activity {
    ArrayList<HashMap<String,Object>> chatList=null;
    String[] from={"image","text"};
    int[] to={R.id.chatlist_image_me,R.id.chatlist_text_me,R.id.chatlist_image_other,R.id.chatlist_text_other};
    int[] layout={R.layout.chat_listitem_me,R.layout.chat_listiem_other};
    public final static int OTHER=1;//标志其他人
    public final static int ME=0;//标志我
    protected EditText editText=null;
    private Button chatSendButton;
    private TextView chatname;
    protected ListView chatListView=null;
    private ImageView ivBack;
    protected MyChatAdapter adapter=null;
    private TextView showmess;
    private  QQinfor  chat_qqinfrom=null;
    private  QQinfor  chat_qqinto=null;
    private  QQinfor  qq_sent=null;
    private  QQinfor  qq_chect=null;
    // private String context1;
    private Messageinfor messcheck;
    private Messageinfor mess;
    private ArrayList<Messageinfor> message;
    private ArrayList<Messageinfor> messagecheck;
    private ArrayList<Messageinfor> messagerecive;
    private String username;
    private String othername;
    private Timer timer;
    TimerTask task;
    private Bitmap userhead=null;
    private Bitmap otherhead=null;
    private String chat_name=null;
    //String all;

    private Handler rHandler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==2)
            {
                chat_qqinto=(QQinfor)msg.obj;
                System.out.print("GetQQinfor--->"+ chat_qqinto);
                if(chat_qqinto.getpid()==305){
                    for (int i = 0; i < chat_qqinto.getMessageinfor_list().size(); i++) {
                        System.out.print(chat_qqinto.getMessageinfor_list().get(i).getFromuid());
                        if(chat_qqinto.getMessageinfor_list().get(i).getFromuid().equals(username)){
                            addTextToList(chat_qqinto.getMessageinfor_list().get(i).getContent(),ME);
                        }
                        else{
                            addTextToList(chat_qqinto.getMessageinfor_list().get(i).getContent(),OTHER);
                        }
                        adapter.notifyDataSetChanged();
                        chatListView.setSelection(chatList.size() - 1);
                    }
                }
                else {
                    for (int i = 0; i < chat_qqinto.getMessageinfor_list().size(); i++) {
                        //chat_qqinto.getMessageinfor_list().get(i).getContent();
                        // all+=chat_qqinto.getMessageinfor_list().get(i).getContent();
                        addTextToList(chat_qqinto.getMessageinfor_list().get(i).getContent(), OTHER);
                        adapter.notifyDataSetChanged();
                        chatListView.setSelection(chatList.size() - 1);
                    }
                }

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chattwo);
        chatList=new ArrayList<HashMap<String,Object>>();//初始化聊天列表
        chatSendButton=(Button)findViewById(R.id.chat_bottom_sendbutton);
        editText=(EditText)findViewById(R.id.chat_bottom_edittext);
        chatListView=(ListView)findViewById(R.id.chat_list);
        ivBack=(ImageView)findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ChattwoActivity.this.finish();
            }
        });
        chatname=(TextView)findViewById(R.id.chat_contact_name);
        adapter=new MyChatAdapter(this,chatList,layout,from,to);
        Intent intent=getIntent();
        mess=new Messageinfor();
        messcheck=new Messageinfor();

        qq_sent=new QQinfor();
        //获取传递过来的QQinfor
        chat_qqinfrom=(QQinfor)intent.getSerializableExtra("QQinfor");
        userhead=(Bitmap)intent.getParcelableExtra("Ubitmap");//获得用户头像
        otherhead=(Bitmap)intent.getParcelableExtra("Obitmap");//好友头像
        chat_name=(String)intent.getStringExtra("Chatname");//获得聊天好友名字
        chatname.setText(chat_name);
        username=chat_qqinfrom.getUid();
        othername=chat_qqinfrom.getFrineds_list().get(0).getUid();//聊天好友名字
        Toast.makeText(this,"youde",Toast.LENGTH_SHORT).show();
        messcheck.setFromuid(othername);//08C4668703C6DE819DBFC505FF368589//95D169954135E42CFD814F3AACBF2B6C
        messcheck.setTouid(username);
        messagecheck=new ArrayList<Messageinfor>();
        messagecheck.add(messcheck);
        timer = new Timer(true);
        qq_chect=new QQinfor();
        qq_chect.setMessageinfor_list(messagecheck);
        task = new TimerTask(){
            public void run() {
                SendGet check=new SendGet(qq_chect,rHandler,202);
                check.start();
            }
        };
        initialize(username,othername);//初始化聊天列表，导入历史信息
        chatSendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String myWord = null;

                /**
                 * 这是一个发送消息的监听器，注意如果文本框中没有内容，那么getText()的返回值可能为
                 * null，这时调用toString()会有异常！所以这里必须在后面加上一个""隐式转换成String实例
                 * ，并且不能发送空消息。
                 */

                myWord =editText.getText().toString();
                if (myWord.length() == 0)
                    return;
                message = new ArrayList<Messageinfor>();
                mess.setFromuid(username);
                mess.setTouid(othername);
                mess.setContent(myWord);
                message.add(mess);
                qq_sent.setMessageinfor_list(message);
                new SendGet(qq_sent, rHandler, 201).start();
                editText.setText("");
                addTextToList(myWord, ME);//由自己发送消息
                /**
                 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
                 */
                adapter.notifyDataSetChanged();
                chatListView.setSelection(chatList.size()-1);

            }
        });
        chatListView.setAdapter(adapter);
    }

    private  void initialize(String uid,String pid){
        QQinfor qq_history=new QQinfor();
        qq_history.setpid(303);
        ArrayList<Messageinfor> messagehistory=new ArrayList<Messageinfor>();
        Messageinfor mess_history=new Messageinfor();
        mess_history.setFromuid(username);
        mess_history.setTouid(othername);
        messagehistory.add(mess_history);
        qq_history.setMessageinfor_list(messagehistory);
        new SendGet(qq_history, rHandler,303).start();
    }
    ///////添加消息
    protected void addTextToList(String text, int who){
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("person",who );
        //map.put("image", who==ME?R.drawable.ic:R.drawable.ne);
        map.put("image", who==ME?userhead:otherhead);
        map.put("text", text);
        chatList.add(map);
    }

    private class MyChatAdapter extends BaseAdapter{

        Context context=null;
        ArrayList<HashMap<String,Object>> chatList=null;
        int[] layout;
        String[] from;
        int[] to;
        public MyChatAdapter(Context context,
                             ArrayList<HashMap<String, Object>> chatList, int[] layout,
                             String[] from, int[] to) {
            super();
            this.context = context;
            this.chatList = chatList;
            this.layout = layout;
            this.from = from;
            this.to = to;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return chatList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder{
            public ImageView imageView=null;
            public TextView textView=null;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder=null;
            int who=(Integer)chatList.get(position).get("person");

            convertView= LayoutInflater.from(context).inflate(
                    layout[who==ME?0:1], null);
            holder=new ViewHolder();
            holder.imageView=(ImageView)convertView.findViewById(to[who*2+0]);
            holder.textView=(TextView)convertView.findViewById(to[who*2+1]);
            System.out.println(holder);
            System.out.println(holder.imageView);
            // holder.imageView.setBackgroundResource((Integer)chatList.get(position).get(from[0]));
            holder.imageView.setImageBitmap((Bitmap)chatList.get(position).get(from[0]));
            holder.textView.setText(chatList.get(position).get(from[1]).toString());
            return convertView;
        }

    }
    //重写方法使timer结束
    @Override
    protected void onStop() {
        //my implementation here
        timer.cancel();
        super.onStop();
    }
    @Override
    protected void onPause() {
        //my implementation here
        timer.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        //my implementation here
        timer.schedule(task, 500, 1000);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //my implementation here
        timer.cancel();
        super.onDestroy();
    }

}
