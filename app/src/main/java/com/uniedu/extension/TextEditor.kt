package com.uniedu.extension

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.uniedu.R
import com.uniedu.utils.ClassSharedPreferences
import jp.wasabeef.richeditor.RichEditor

fun View.setupTextEditor(mEditor: RichEditor) {
    val ctx = this.context
    val prefs = ClassSharedPreferences(ctx)
    
//    mEditor.setEditorHeight(300)
    mEditor.setEditorFontSize(18)
    mEditor.setEditorFontColor(Color.BLACK)
    //mEditor.setEditorBackgroundColor(Color.BLUE);
    //mEditor.setBackgroundColor(Color.BLUE);
    //mEditor.setBackgroundResource(R.drawable.bg);
    //mEditor.setEditorBackgroundColor(Color.BLUE);
    //mEditor.setBackgroundColor(Color.BLUE);
    //mEditor.setBackgroundResource(R.drawable.bg);
    mEditor.setPadding(2, 2, 2, 2)
    //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
    //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
    mEditor.setPlaceholder("Write here...")
//        mEditor.html = ""
//        mEditor.clearMatches()

    //mEditor.setInputEnabled(false);


    //mEditor.setInputEnabled(false);
//    val imgSrcRgx = "src\\s*=\\s*['\"]([^'\"]+)['\"]".toRegex(RegexOption.IGNORE_CASE)
    val imgSrcRgx = "src=\"(.*?)\"".toRegex(RegexOption.IGNORE_CASE)
    mEditor.setOnTextChangeListener {text->

        val imgFromBody = imgSrcRgx.findAll(text).map {it.value.replace("src=","").replace("\"","")}.toList()
//        this.findViewById<TextView>(R.id.preview).text = text
//        this.findViewById<TextView>(R.id.preview).text = "$imgFromBody"
    }

    this.findViewById<View>(R.id.action_undo).setOnClickListener { mEditor.undo() }

    this.findViewById<View>(R.id.action_redo).setOnClickListener { mEditor.redo() }

    this.findViewById<View>(R.id.action_bold).setOnClickListener { mEditor.setBold() }

    this.findViewById<View>(R.id.action_Italic).setOnClickListener { mEditor.setItalic() }

    this.findViewById<View>(R.id.action_subscript).setOnClickListener {
        mEditor.setSubscript()
//        mEditor.clearFocusEditor()
//        mEditor.html = mEditor.html
//        mEditor.clearFocus()
    }

    this.findViewById<View>(R.id.action_superscript).setOnClickListener { mEditor.setSuperscript() }

    this.findViewById<View>(R.id.action_strikethrough).setOnClickListener { mEditor.setStrikeThrough() }

    this.findViewById<View>(R.id.action_underline).setOnClickListener { mEditor.setUnderline() }

    this.findViewById<View>(R.id.action_heading1).setOnClickListener {mEditor.setHeading(1)}
//
    this.findViewById<View>(R.id.action_heading2).setOnClickListener {mEditor.setHeading(2)}
//
    this.findViewById<View>(R.id.action_heading3).setOnClickListener {mEditor.setHeading(3)}

    this.findViewById<View>(R.id.action_heading4).setOnClickListener { mEditor.setHeading( 4)}

    this.findViewById<View>(R.id.action_heading5).setOnClickListener {mEditor.setHeading(5)}

    this.findViewById<View>(R.id.action_heading6).setOnClickListener {mEditor.setHeading(6) }

    this.findViewById<View>(R.id.action_txt_color).setOnClickListener(object :View.OnClickListener {
        private var isChanged = false
        override fun onClick(v: View) {
            mEditor.setTextColor(if (isChanged) Color.BLACK else Color.RED)
            isChanged = !isChanged
        }
    })

    this.findViewById<View>(R.id.action_bg_color).setOnClickListener(object :
        View.OnClickListener {
        private var isChanged = false
        override fun onClick(v: View) {
            mEditor.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
            isChanged = !isChanged
        }
    })

    this.findViewById<View>(R.id.action_indent).setOnClickListener { mEditor.setIndent() }

    this.findViewById<View>(R.id.action_outdent).setOnClickListener { mEditor.setOutdent() }

    this.findViewById<View>(R.id.action_align_left).setOnClickListener { mEditor.setAlignLeft() }

    this.findViewById<View>(R.id.action_align_center).setOnClickListener { mEditor.setAlignCenter() }

    this.findViewById<View>(R.id.action_align_right).setOnClickListener { mEditor.setAlignRight() }

    this.findViewById<View>(R.id.action_blockquote).setOnClickListener { mEditor.setBlockquote() }

    this.findViewById<View>(R.id.action_insert_bullets).setOnClickListener { mEditor.setBullets() }

    this.findViewById<View>(R.id.action_insert_numbers).setOnClickListener { mEditor.setNumbers() }

    this.findViewById<View>(R.id.action_insert_image).setOnClickListener {
//            mEditor.insertImage(
//                "https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg",
//                "dachshund", 320
//            )
        mEditor.insertImage(
            prefs.getImgUploadPath(),
            "dachshund", 320
        )
    }

    this.findViewById<View>(R.id.action_insert_youtube).setOnClickListener {
        mEditor.insertYoutubeVideo(
            "https://www.youtube.com/embed/pS5peqApgUA"
        )
    }

    this.findViewById<View>(R.id.action_insert_audio).setOnClickListener {
        mEditor.insertAudio(
            "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_5MG.mp3"
        )
    }

    this.findViewById<View>(R.id.action_insert_video).setOnClickListener {
        mEditor.insertVideo(
            "https://test-videos.co.uk/vids/" +
                    "bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_10MB.mp4",
            360
        )
    }

    this.findViewById<View>(R.id.action_insert_link).setOnClickListener {
        mEditor.insertLink(
            "https://github.com/wasabeef",
            "wasabeef"
        )
    }
    this.findViewById<View>(R.id.action_insert_checkbox).setOnClickListener { mEditor.insertTodo() }

}