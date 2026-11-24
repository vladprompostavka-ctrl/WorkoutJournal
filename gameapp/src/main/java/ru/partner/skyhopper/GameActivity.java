package ru.partner.skyhopper;
import android.app.Activity; import android.os.Bundle; import android.view.*; import android.graphics.*; import android.content.Context; import java.util.*;
public class GameActivity extends Activity {
 public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);setContentView(new GameView(this));}
 static class Plat{float x,y,w,s;boolean moving;}
 class GameView extends View{
  Paint p=new Paint(3);Random rnd=new Random();ArrayList<Plat> ps=new ArrayList<>();float x,y,vy,score,best,dir;boolean dead;long last;
  GameView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
  void reset(){ps.clear();score=0;vy=0;dead=false;x=getWidth()/2f;y=getHeight()*.68f;for(int i=0;i<9;i++){Plat a=new Plat();a.w=82+rnd.nextInt(28);a.x=rnd.nextInt(Math.max(1,getWidth()-(int)a.w));a.y=getHeight()*.88f-i*145;a.moving=i>3&&i%4==0;a.s=1+rnd.nextFloat()*1.5f;ps.add(a);}invalidate();}
  protected void onSizeChanged(int w,int h,int ow,int oh){if(ps.isEmpty())reset();}
  void label(Canvas c,String s,float xx,float yy,int z,int col){p.setColor(col);p.setTextSize(z);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextAlign(Paint.Align.CENTER);c.drawText(s,xx,yy,p);}
  protected void onDraw(Canvas c){int w=getWidth(),h=getHeight();p.setShader(new LinearGradient(0,0,0,h,0xff9be7ff,0xfff5fcff,Shader.TileMode.CLAMP));c.drawPaint(p);p.setShader(null);
   p.setColor(0x88ffffff);for(int i=0;i<5;i++){float xx=(i*193+40)%w,yy=(i*257+(score*.2f)%(h+100))%(h+100);c.drawCircle(xx,yy,25,p);c.drawCircle(xx+28,yy-8,30,p);c.drawCircle(xx+55,yy,22,p);}
   for(Plat a:ps){p.setColor(a.moving?0xffffa63d:0xff2fb66b);c.drawRoundRect(a.x,a.y,a.x+a.w,a.y+16,8,8,p);}
   p.setColor(0xff2456a6);c.drawRoundRect(x-18,y-25,x+18,y+20,12,12,p);p.setColor(0xffffd34e);c.drawCircle(x,y-27,17,p);p.setColor(0xff172d50);c.drawCircle(x-6,y-29,3,p);c.drawCircle(x+6,y-29,3,p);
   p.setColor(0xddffffff);c.drawRoundRect(15,15,150,62,15,15,p);label(c,"★ "+(int)score,82,47,22,0xff153a57);
   if(dead){p.setColor(0xaa12334a);c.drawRect(0,0,w,h,p);label(c,"ПРЫЖОК ОКОНЧЕН",w/2f,h*.42f,27,Color.WHITE);label(c,"Счёт: "+(int)score+"  Рекорд: "+(int)best,w/2f,h*.48f,19,Color.WHITE);p.setColor(0xffffcf4a);c.drawRoundRect(w*.24f,h*.54f,w*.76f,h*.62f,20,20,p);label(c,"ИГРАТЬ СНОВА",w/2f,h*.595f,18,0xff183b58);}
   else{long now=System.currentTimeMillis();if(last!=0){float dt=Math.min(2,(now-last)/16f);tick(dt);}last=now;postInvalidateDelayed(16);}
  }
  void tick(float dt){if(dead)return;x+=dir*7*dt;if(x< -18)x=getWidth()+18;if(x>getWidth()+18)x=-18;vy+=.42f*dt;y+=vy*dt;
   if(vy>0)for(Plat a:ps)if(x+13>a.x&&x-13<a.x+a.w&&y+20>=a.y&&y+20<=a.y+vy*dt+18){vy=-12.5f;break;}
   float top=getHeight()*.38f;if(y<top){float d=top-y;y=top;for(Plat a:ps)a.y+=d;score+=d;}
   for(Plat a:ps)if(a.moving){a.x+=a.s*dt;if(a.x<0||a.x+a.w>getWidth())a.s=-a.s;}
   for(int i=ps.size()-1;i>=0;i--)if(ps.get(i).y>getHeight()+20){ps.remove(i);Plat a=new Plat();a.w=82+rnd.nextInt(28);a.x=rnd.nextInt(Math.max(1,getWidth()-(int)a.w));a.y=-30-rnd.nextInt(70);a.moving=rnd.nextInt(5)==0;a.s=1+rnd.nextFloat()*1.5f;ps.add(a);}
   if(y>getHeight()+50){dead=true;best=Math.max(best,score);}
  }
  public boolean onTouchEvent(android.view.MotionEvent e){float xx=e.getX(),yy=e.getY();if(e.getAction()==0){if(dead){if(yy>getHeight()*.5f)reset();return true;}dir=xx<getWidth()/2f?-1:1;return true;}if(e.getAction()==2){dir=xx<getWidth()*.43f?-1:xx>getWidth()*.57f?1:0;return true;}if(e.getAction()==1){dir=0;return true;}return true;}
 }
}
