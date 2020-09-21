package com.uniedu.ui.fragment.bottomsheet

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.uniedu.R
import com.uniedu.databinding.FragmentEBookDetailBinding
import com.uniedu.ui.fragment.BaseFragmentBottomSheet
import jp.wasabeef.richeditor.RichEditor


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentEBookDetail : BaseFragmentBottomSheet() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding:FragmentEBookDetailBinding


    private lateinit var mEditor: RichEditor
    private lateinit var mPreview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_e_book_detail, container, false)
        // Inflate the layout for this fragment

        mEditor = binding.editor
        mEditor.setEditorHeight(200)
        mEditor.setEditorFontSize(22)
        mEditor.setEditorFontColor(Color.RED)
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10)
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...")
        //mEditor.setInputEnabled(false);


        //mEditor.setInputEnabled(false);
        mPreview = binding.root.findViewById<View>(R.id.preview) as TextView
        mEditor.setOnTextChangeListener { text -> mPreview.text = text }

        binding.root.findViewById<View>(R.id.action_undo).setOnClickListener { mEditor.undo() }

        binding.root.findViewById<View>(R.id.action_redo).setOnClickListener { mEditor.redo() }

        binding.root.findViewById<View>(R.id.action_bold).setOnClickListener { mEditor.setBold() }

        binding.root.findViewById<View>(R.id.action_Italic).setOnClickListener { mEditor.setItalic() }

        binding.root.findViewById<View>(R.id.action_subscript).setOnClickListener { /*mEditor.setSubscript()*/ mEditor.focusEditor() }

        binding.root.findViewById<View>(R.id.action_superscript).setOnClickListener { mEditor.setSuperscript() }

        binding.root.findViewById<View>(R.id.action_strikethrough).setOnClickListener { mEditor.setStrikeThrough() }

        binding.root.findViewById<View>(R.id.action_underline).setOnClickListener { mEditor.setUnderline() }

        binding.root.findViewById<View>(R.id.action_heading1).setOnClickListener {mEditor.setHeading(1)}

        binding.root.findViewById<View>(R.id.action_heading2).setOnClickListener {mEditor.setHeading(2)}

        binding.root.findViewById<View>(R.id.action_heading3).setOnClickListener {
            mEditor.setHeading(
                3
            )
        }

        binding.root.findViewById<View>(R.id.action_heading4).setOnClickListener {
            mEditor.setHeading(
                4
            )
        }

        binding.root.findViewById<View>(R.id.action_heading5).setOnClickListener {
            mEditor.setHeading(
                5
            )
        }

        binding.root.findViewById<View>(R.id.action_heading6).setOnClickListener {
            mEditor.setHeading(
                6
            )
        }

        binding.root.findViewById<View>(R.id.action_txt_color).setOnClickListener(object :View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                mEditor.setTextColor(if (isChanged) Color.BLACK else Color.RED)
                isChanged = !isChanged
            }
        })

        binding.root.findViewById<View>(R.id.action_bg_color).setOnClickListener(object :
            View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                mEditor.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
                isChanged = !isChanged
            }
        })

        binding.root.findViewById<View>(R.id.action_indent).setOnClickListener { mEditor.setIndent() }

        binding.root.findViewById<View>(R.id.action_outdent).setOnClickListener { mEditor.setOutdent() }

        binding.root.findViewById<View>(R.id.action_align_left).setOnClickListener { mEditor.setAlignLeft() }

        binding.root.findViewById<View>(R.id.action_align_center).setOnClickListener { mEditor.setAlignCenter() }

        binding.root.findViewById<View>(R.id.action_align_right).setOnClickListener { mEditor.setAlignRight() }

        binding.root.findViewById<View>(R.id.action_blockquote).setOnClickListener { mEditor.setBlockquote() }

        binding.root.findViewById<View>(R.id.action_insert_bullets).setOnClickListener { mEditor.setBullets() }

        binding.root.findViewById<View>(R.id.action_insert_numbers).setOnClickListener { mEditor.setNumbers() }

        binding.root.findViewById<View>(R.id.action_insert_image).setOnClickListener {
//            mEditor.insertImage(
//                "https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg",
//                "dachshund", 320
//            )
            mEditor.insertImage(
                prefs.getImgUploadPath(),
                "dachshund", 320
            )
        }

        binding.root.findViewById<View>(R.id.action_insert_youtube).setOnClickListener {
            mEditor.insertYoutubeVideo(
                "https://www.youtube.com/embed/pS5peqApgUA"
            )
        }

        binding.root.findViewById<View>(R.id.action_insert_audio).setOnClickListener {
            mEditor.insertAudio(
                "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_5MG.mp3"
            )
        }

        binding.root.findViewById<View>(R.id.action_insert_video).setOnClickListener {
            mEditor.insertVideo(
                "https://test-videos.co.uk/vids/" +
                        "bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_10MB.mp4",
                360
            )
        }

        binding.root.findViewById<View>(R.id.action_insert_link).setOnClickListener {
            mEditor.insertLink(
                "https://github.com/wasabeef",
                "wasabeef"
            )
        }
        binding.root.findViewById<View>(R.id.action_insert_checkbox).setOnClickListener { mEditor.insertTodo() }


        return binding.root
    }




    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =  bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }
    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentEBookDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentEBookDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}