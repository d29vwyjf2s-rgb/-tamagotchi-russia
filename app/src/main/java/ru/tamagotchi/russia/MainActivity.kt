package ru.tamagotchi.russia

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.graphics.*
import android.view.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(GameView(this)) }
}

class GameView(private val ctx: Context) : View(ctx) {
    private val prefs = ctx.getSharedPreferences("pet", Context.MODE_PRIVATE)
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private var food=prefs.getInt("food",72); private var happy=prefs.getInt("happy",68)
    private var energy=prefs.getInt("energy",70); private var clean=prefs.getInt("clean",65)
    private var health=prefs.getInt("health",90); private var age=prefs.getInt("age",1)
    private var asleep=prefs.getBoolean("sleep",false); private var mini=false
    private var message="Привет! Я твой питомец!"; private var target=Random.nextInt(3)
    init { p.typeface=Typeface.MONOSPACE; setBackgroundColor(Color.rgb(16,16,16)) }

    override fun onDraw(c: Canvas) {
        val w=width.toFloat(); val h=height.toFloat(); p.textAlign=Paint.Align.CENTER
        p.color=Color.rgb(25,25,25); c.drawRoundRect(18f,18f,w-18f,h-18f,18f,18f,p)
        p.color=Color.WHITE; p.textSize=25f; c.drawText("ТАМАГОЧИ",w/2,55f,p)
        p.textSize=15f; p.color=Color.LTGRAY; c.drawText(if(asleep) "🌙 НОЧЬ" else "☀ ДЕНЬ",w/2,80f,p); c.drawText("Пушок • $age день",w/2,104f,p)
        drawPet(c,w/2,205f)
        if(mini){ drawMini(c,w,h); return }
        stat(c,"Сытость",food,130f); stat(c,"Счастье",happy,158f); stat(c,"Энергия",energy,186f); stat(c,"Чистота",clean,214f); stat(c,"Здоровье",health,242f)
        p.color=Color.rgb(255,213,79); p.textSize=15f; c.drawText(message,w/2,285f,p)
        val labels=arrayOf("🍖 КОРМИТЬ","🎮 ИГРАТЬ","🧹 УБРАТЬ","❤️ ЛЕЧИТЬ","😴 СПАТЬ")
        for(i in labels.indices){ val row=i/2; val col=i%2; val x=30f+col*(w-60f)/2; val y=315f+row*60f; val r=x+(w-70f)/2; p.color=Color.rgb(48,48,48); c.drawRoundRect(x,y,r,y+45f,9f,9f,p); p.color=Color.WHITE;p.textSize=13f;c.drawText(labels[i],(x+r)/2,y+28f,p) }
        p.color=Color.GRAY;p.textSize=11f;c.drawText("Нажми на питомца — он подскажет, что ему нужно",w/2,h-32f,p)
    }
    private fun drawPet(c:Canvas,x:Float,y:Float){ p.color=if(asleep)Color.rgb(75,75,105) else Color.rgb(255,213,79);c.drawRoundRect(x-88,y-68,x+88,y+68,22f,22f,p);p.color=Color.rgb(30,30,30);if(asleep){p.textSize=32f;c.drawText("Z z z",x,y+10,p)}else{c.drawRect(x-50,y-23,x-29,y+2,p);c.drawRect(x+29,y-23,x+50,y+2,p);c.drawRect(x-27,y+28,x+27,y+38,p)};p.color=Color.rgb(255,160,80);c.drawCircle(x-70,y-50,14f,p);c.drawCircle(x+70,y-50,14f,p)}
    private fun stat(c:Canvas,label:String,v:Int,y:Float){val l=55f;r@run{val r=width-55f;p.color=Color.DKGRAY;c.drawRoundRect(l,y,r,y+14f,7f,7f,p);p.color=Color.rgb(255,213,79);c.drawRoundRect(l,y,l+(r-l)*v/100f,y+14f,7f,7f,p);p.color=Color.WHITE;p.textAlign=Paint.Align.LEFT;p.textSize=11f;c.drawText("$label: $v%",l,y-3,p);p.textAlign=Paint.Align.CENTER}}
    private fun drawMini(c:Canvas,w:Float,h:Float){p.color=Color.WHITE;p.textSize=21f;c.drawText("МИНИ-ИГРА",w/2,315f,p);p.color=Color.LTGRAY;p.textSize=14f;c.drawText("Найди правильную кнопку!",w/2,345f,p);val n=arrayOf("ЛЕВО","ЦЕНТР","ПРАВО");for(i in 0..2){val x=25f+i*(w-50f)/3;p.color=Color.rgb(50,50,50);c.drawRoundRect(x,380f,x+(w-70f)/3,440f,9f,9f,p);p.color=Color.WHITE;c.drawText(n[i],x+(w-70f)/6,416f,p)}}
    override fun onTouchEvent(e:MotionEvent):Boolean{if(e.action!=MotionEvent.ACTION_UP)return true;val x=e.x;val y=e.y;if(!mini&&y in 110f..265f){message=if(happy>50)"Мне хорошо! 😊" else "Поиграй со мной!";invalidate();return true};if(mini){if(y in 370f..460f){val chosen=when{ x<width/3f->0;x<width*2/3f->1;else->2};if(chosen==target){happy=min(100,happy+18);energy=max(0,energy-8);message="Ура! Получилось!"}else{happy=max(0,happy-5);message="Почти! Попробуй ещё."};mini=false;target=Random.nextInt(3);save();invalidate()};return true};when{y in 300f..365f&&x<width/2->feed();y in 300f..365f->play();y in 365f..425f&&x<width/2->cleanPet();y in 365f..425f->heal();y in 425f..500f->sleep()};invalidate();return true}
    private fun feed(){food=min(100,food+20);health=min(100,health+2);message="Вкусно! 🍖";save()}
    private fun play(){if(energy<10)message="Я устал. Дай мне поспать 😴" else {mini=true;message="Поиграем!"}}
    private fun cleanPet(){clean=min(100,clean+30);health=min(100,health+3);message="Теперь я чистенький!";save()}
    private fun heal(){health=min(100,health+20);message="Мне уже лучше ❤️";save()}
    private fun sleep(){asleep=!asleep;if(asleep)message="Спокойной ночи! 🌙" else {energy=min(100,energy+35);message="Доброе утро! ☀";save()};invalidate()}
    private fun save(){prefs.edit().putInt("food",food).putInt("happy",happy).putInt("energy",energy).putInt("clean",clean).putInt("health",health).putInt("age",age).putBoolean("sleep",asleep).apply()}
    override fun onDetachedFromWindow(){save();super.onDetachedFromWindow()}
}
