package com.todokanai.displayalarm.tools.independent.unstable

import android.view.View

/** class for dragArea feature (Windows의 클릭-드래그 범위지정에 해당) **/
class DragAreaAddOn() {
    /** drag 활성화 여부 **/
    private var enabled:Boolean = false

    private var isStartReady = false
    private var startX : Float = 0f
    private var startY : Float = 0f

    /** a longClickListener preset for [startDragArea] **/
    val longClickListener = View.OnLongClickListener {
        /*
        isStartReady = false
        onDragStart()
        it.setOnTouchListener { view, motionEvent ->
            if (isStartReady == false) {
                startX = motionEvent.x
                startY = motionEvent.y
                isStartReady = true
            }
            false
        }
        false
         */
        startDragArea(it)
        false
    }

    /** dragArea event를 시작하는 callback
     *
     * @param view View to invoke event from
     * **/
    fun startDragArea(view: View){
        isStartReady = false
        enabled = true
        view.setOnTouchListener { _, motionEvent ->
            if (isStartReady == false) {
                startX = motionEvent.x
                startY = motionEvent.y
                isStartReady = true
            }
            false
        }
    }

    private val onTouchListener = View.OnTouchListener { view, motionEvent ->
        if(enabled){
            if(motionEvent.action == 1){
                onGetArea(startX,startY,motionEvent.x,motionEvent.y)
                enabled = false
            }else{
                onMove(startX,startY,motionEvent.x,motionEvent.y)
            }
        }
        false

    }

    /**
     * drag 동작 적용 가능한 범위 (view) 지정
     * @param view View Area to enable drag event
     * **/
    fun setAreaRange(view: View){
        view.setOnTouchListener(onTouchListener)
    }
    /** called during dragArea event**/
    fun onMove(startX:Float,startY:Float,endX:Float,endY:Float){

    }

    /** called when dragArea event has ended **/
    fun onGetArea(startX:Float,startY:Float,endX:Float,endY:Float){

    }
}