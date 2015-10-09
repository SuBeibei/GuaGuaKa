#### GuaGuaKa-自定义刮刮卡控件

#####属性说明
- strokeWidth: 刮卡时的划痕大小
- maskDrawable: 覆盖层的图像或颜色
- type: 被覆盖层的类型，drawable为图像, text为文字
- back_image: 被覆盖层的图像
- text: 被覆盖层的文字
- textSize: 被覆盖层的文字大小
- textColor: 被覆盖层的文字颜色
- autoClear: 布尔值，当刮到一定程度的时候是否覆盖层自动消失

#####例子说明
- 例子一

    &lt;com.example.pc.guaguaka.GuaGuaKa               
      &nbsp; &nbsp; &nbsp; &nbsp;ggk:back_image="@drawable/landscape"    
      &nbsp; &nbsp; &nbsp; &nbsp;ggkggk:strokeWidth="60"    
      &nbsp; &nbsp; &nbsp; &nbsp;ggk:maskDrawable="#c0c0c0"    
      &nbsp; &nbsp; &nbsp; &nbsp;android:layout_gravity="center_horizontal"    
      &nbsp; &nbsp; &nbsp; &nbsp;android:layout_margin="10dp"    
      &nbsp; &nbsp; &nbsp; &nbsp;android:layout_width="200dp"    
      &nbsp; &nbsp; &nbsp; &nbsp;android:layout_height="200dp" /&gt;


- 例子二

    &lt;com.example.pc.guaguaka.GuaGuaKa        
      &nbsp; &nbsp; &nbsp; &nbsp;ggk:type="text"         
      &nbsp; &nbsp; &nbsp; &nbsp;ggk:text="500000"         
      &nbsp; &nbsp; &nbsp; &nbsp;ggk:textSize="25sp"       
      &nbsp; &nbsp; &nbsp; &nbsp;ggk:strokeWidth="60"        
      &nbsp; &nbsp; &nbsp; &nbsp;ggk:maskDrawable="@drawable/landscape"      
      &nbsp; &nbsp; &nbsp; &nbsp;ggk:autoClear="true"      
      &nbsp; &nbsp; &nbsp; &nbsp;android:layout_gravity="center_horizontal"      
      &nbsp; &nbsp; &nbsp; &nbsp;android:layout_margin="10dp"      
      &nbsp; &nbsp; &nbsp; &nbsp;android:layout_width="200dp"      
      &nbsp; &nbsp; &nbsp; &nbsp;android:layout_height="100dp" /&gt;     
