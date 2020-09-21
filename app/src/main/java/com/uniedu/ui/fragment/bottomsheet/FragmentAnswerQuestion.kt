package com.uniedu.ui.fragment.bottomsheet


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.github.irshulx.Editor
import com.github.irshulx.EditorListener
import com.github.irshulx.models.EditorTextStyle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.uniedu.R
import com.uniedu.databinding.FragmentAnswerQuestionBinding
import com.uniedu.model.Questions
import com.uniedu.ui.fragment.BaseFragment
import com.uniedu.ui.fragment.BaseFragmentBottomSheet
import top.defaults.colorpicker.ColorPickerPopup
import java.io.IOException

import java.util.HashMap;
import java.util.Locale;

private const val QUESTION = "question"


class FragmentAnswerQuestion : BaseFragmentBottomSheet() {
    private var question: Questions? = null
    lateinit var binding:FragmentAnswerQuestionBinding

    var editor: Editor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        arguments?.let {
            question = it.getParcelable(QUESTION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_answer_question, container,false)
        editor =  binding.editor
        setUpEditor();


        return binding.root
    }

    private fun setUpEditor() {
        binding.root.findViewById<View>(R.id.action_h1).setOnClickListener {
            editor!!.updateTextStyle(
                EditorTextStyle.H1
            )
        }
        binding.root.findViewById<View>(R.id.action_h2).setOnClickListener {
            editor!!.updateTextStyle(
                EditorTextStyle.H2
            )
        }
        binding.root.findViewById<View>(R.id.action_h3).setOnClickListener {
            editor!!.updateTextStyle(
                EditorTextStyle.H3
            )
        }
        binding.root.findViewById<View>(R.id.action_bold).setOnClickListener {
            editor!!.updateTextStyle(
                EditorTextStyle.BOLD
            )
        }
        binding.root.findViewById<View>(R.id.action_Italic).setOnClickListener {
            editor!!.updateTextStyle(
                EditorTextStyle.ITALIC
            )
        }
        binding.root.findViewById<View>(R.id.action_indent).setOnClickListener {
            editor!!.updateTextStyle(
                EditorTextStyle.INDENT
            )
        }
        binding.root.findViewById<View>(R.id.action_blockquote).setOnClickListener {
            editor!!.updateTextStyle(
                EditorTextStyle.BLOCKQUOTE
            )
        }
        binding.root.findViewById<View>(R.id.action_outdent).setOnClickListener {
            editor!!.updateTextStyle(
                EditorTextStyle.OUTDENT
            )
        }
        binding.root.findViewById<View>(R.id.action_bulleted).setOnClickListener { editor!!.insertList(false) }
        binding.root.findViewById<View>(R.id.action_unordered_numbered).setOnClickListener {
            editor!!.insertList(
                true
            )
        }
        binding.root.findViewById<View>(R.id.action_hr).setOnClickListener { editor!!.insertDivider() }
        binding.root.findViewById<View>(R.id.action_color).setOnClickListener {
            ColorPickerPopup.Builder(thisContext)
                .initialColor(Color.RED) // Set initial color
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(binding.root.findViewById(R.id.content), object : ColorPickerPopup.ColorPickerObserver {
                    override fun onColorPicked(color: Int) {
                        Toast.makeText(
                            thisContext,
                            "picked" + colorHex(color),
                            Toast.LENGTH_LONG
                        ).show()
                        editor!!.updateTextColor(colorHex(color))
                    }

                    override fun onColor(color: Int, fromUser: Boolean) {}
                })
        }
        binding.root.findViewById<View>(R.id.action_insert_image).setOnClickListener { editor!!.openImagePicker() }
        binding.root.findViewById<View>(R.id.action_insert_link).setOnClickListener { editor!!.insertLink() }
        binding.root.findViewById<View>(R.id.action_erase).setOnClickListener { editor!!.clearAllContents() }
        //editor.dividerBackground=R.drawable.divider_background_dark;
        //editor.setFontFace(R.string.fontFamily__serif);
        val headingTypeface = headingTypeface
        val contentTypeface = contentface
        editor!!.headingTypeface = headingTypeface
        editor!!.contentTypeface = contentTypeface
        editor!!.setDividerLayout(R.layout.tmpl_divider_layout)
        editor!!.setEditorImageLayout(R.layout.tmpl_image_view)
        editor!!.setListItemLayout(R.layout.tmpl_list_item)
        //editor.setNormalTextSize(10);
        // editor.setEditorTextColor("#FF3333");
        //editor.StartEditor();
        editor!!.editorListener = object : EditorListener {
            override fun onTextChanged(editText: EditText, text: Editable) {
                // Toast.makeText(EditorTestActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            override fun onUpload(image: Bitmap, uuid: String) {
                Toast.makeText(thisContext, uuid, Toast.LENGTH_LONG).show()
                /**
                 * TODO do your upload here from the bitmap received and all onImageUploadComplete(String url); to insert the result url to
                 * let the editor know the upload has completed
                 */
                editor!!.onImageUploadComplete(
                    "http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg",
                    uuid
                )
                // editor.onImageUploadFailed(uuid);
            }

            override fun onRenderMacro(
                name: String,
                props: Map<String, Any>,
                index: Int
            ): View {
                return layoutInflater.inflate(R.layout.layout_authored_by, null)
            }
        }
        /**
         * rendering serialized content
         * //  */
        //  String serialized = "{\"nodes\":[{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003etextline 1 a great time and I will branch office is closed on Sundays\\u003c/p\\u003e\\n\"],\"contentStyles\":[\"H1\"],\"textSettings\":{\"textColor\":\"#c00000\"},\"type\":\"INPUT\"},{\"content\":[],\"type\":\"hr\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003ethe only one that you have received the stream free and open minded person to discuss a business opportunity to discuss my background.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"childs\":[{\"content\":[\"it is a great weekend and we will have the same to me that the same a great time\"],\"contentStyles\":[\"BOLD\"],\"textSettings\":{\"textColor\":\"#FF0000\"},\"type\":\"IMG_SUB\"}],\"content\":[\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\"],\"type\":\"img\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eI have a place where I have a great time and I will branch manager state to boast a new job in a few weeks and we can host or domain to get to know.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"childs\":[{\"content\":[\"the stream of water in a few weeks and we can host in the stream free and no ippo\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#5E5E5E\"},\"type\":\"IMG_SUB\"}],\"content\":[\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\"],\"type\":\"img\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is that I can get it done today will online at location and I am not a big difference to me so that we are headed \\u003ca href\\u003d\\\"www.google.com\\\"\\u003ewww.google.com\\u003c/a\\u003e it was the only way I.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is not a good day to get the latest version to blame it to the product the.\\u003c/p\\u003e\\n\"],\"contentStyles\":[\"BOLDITALIC\"],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is that I can send me your email to you and I am not able a great time and consideration I have to do the needful.\\u003c/p\\u003e\\n\"],\"contentStyles\":[\"INDENT\"],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eI will be a while ago to a great weekend a great time with the same.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"}]}";
//        String serialized = "{\"nodes\":[{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003e\\u003cspan style\\u003d\\\"color:#000000;\\\"\\u003e\\u003cspan style\\u003d\\\"color:#000000;\\\"\\u003eit is not available beyond that statue in a few days and then we could\\u003c/span\\u003e\\u003c/span\\u003e\\u003c/p\\u003e\\n\"],\"contentStyles\":[\"H1\"],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[],\"type\":\"hr\"},{\"content\":[\"author-tag\"],\"macroSettings\":{\"data-author-name\":\"Alex Wong\",\"data-tag\":\"macro\",\"data-date\":\"12 July 2018\"},\"type\":\"macro\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is a free trial to get a great weekend a good day to you u can do that for.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is that I have to do the needful as early in life is not available beyond my imagination to be a good.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"childs\":[{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003e\\u003cb\\u003eit is not available in the next week or two and I have a place where I\\u003c/b\\u003e\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#006AFF\"},\"type\":\"IMG_SUB\"}],\"content\":[\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\"],\"type\":\"img\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is not available in the next week to see you tomorrow morning to see you then.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[],\"type\":\"hr\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is not available in the next day delivery to you soon with it and.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"}]}";
        // EditorContent des = editor.getContentDeserialized(serialized);
        // editor.render(des);

//        Intent intent = new Intent(getApplicationContext(), RenderTestActivity.class);
//        intent.putExtra("content", serialized);
//        startActivity(intent);
        /**
         * Rendering html
         */
        //render();
        //editor.render();  // this method must be called to start the editor
//        val text =
//            "<h1 data-tag=\"input\" style=\"color:#c00000;\"><span style=\"color:#C00000;\">textline 1 a great time and I will branch office is closed on Sundays</span></h1><hr data-tag=\"hr\"/><p data-tag=\"input\" style=\"color:#000000;\">the only one that you have received the stream free and open minded person to discuss a business opportunity to discuss my background.</p><div data-tag=\"img\"><img src=\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\" /><p data-tag=\"img-sub\" style=\"color:#FF0000;\" class=\"editor-image-subtitle\"><b>it is a great weekend and we will have the same to me that the same a great time</b></p></div><p data-tag=\"input\" style=\"color:#000000;\">I have a place where I have a great time and I will branch manager state to boast a new job in a few weeks and we can host or domain to get to know.</p><div data-tag=\"img\"><img src=\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\" /><p data-tag=\"img-sub\" style=\"color:#5E5E5E;\" class=\"editor-image-subtitle\">the stream of water in a few weeks and we can host in the stream free and no ippo</p></div><p data-tag=\"input\" style=\"color:#000000;\">it is that I can get it done today will online at location and I am not a big difference to me so that we are headed <a href=\"www.google.com\">www.google.com</a> it was the only way I.</p><blockquote data-tag=\"input\" style=\"color:#000000;\">I have to do the negotiation and a half years old story and I am looking forward in a few days.</blockquote><p data-tag=\"input\" style=\"color:#000000;\">it is not a good day to get the latest version to blame it to the product the.</p><ol data-tag=\"ol\"><li data-tag=\"list-item-ol\"><span style=\"color:#000000;\">it is that I can send me your email to you and I am not able a great time and consideration I have to do the needful.</span></li><li data-tag=\"list-item-ol\"><span style=\"color:#000000;\">I have to do the needful and send to me and</span></li><li data-tag=\"list-item-ol\"><span style=\"color:#000000;\">I will be a while ago to a great weekend a great time with the same.</span></li></ol><p data-tag=\"input\" style=\"color:#000000;\">it was u can do to make an offer for a good day I u u have been working with a new job to the stream free and no.</p><p data-tag=\"input\" style=\"color:#000000;\">it was u disgraced our new home in time to get the chance I could not find a good idea for you have a great.</p><p data-tag=\"input\" style=\"color:#000000;\">I have to do a lot to do the same a great time and I have a great.</p><p data-tag=\"input\" style=\"color:#000000;\"></p>"
        //editor.render("<p>Hello man, whats up!</p>");
        //String text = "<p data-tag=\"input\" style=\"color:#000000;\">I have to do the needful and send to me and my husband is in a Apple has to offer a variety is not a.</p><p data-tag=\"input\" style=\"color:#000000;\">I have to go with you will be highly grateful if we can get the latest</p><blockquote data-tag=\"input\" style=\"color:#000000;\">I have to do the negotiation and a half years old story and I am looking forward in a few days.</blockquote><p data-tag=\"input\" style=\"color:#000000;\">I have to do the needful at your to the product and the other to a new job is going well and that the same old stuff and a half day city is the stream and a good idea to get onboard the stream.</p>";
//        editor!!.render(text)


//        binding.root.findViewById<View>(R.id.btnRender).setOnClickListener { /*
//                        Retrieve the content as serialized, you could also say getContentAsHTML();
//                        */
//            val text = editor!!.contentAsSerialized
//            editor!!.contentAsHTML
//            val intent = Intent(thisContext, RenderTestActivity::class.java)
//            intent.putExtra("content", text)
//            startActivity(intent)
//        }
        /**
         * Since the endusers are typing the content, it's always considered good idea to backup the content every specific interval
         * to be safe.
         *
         * private final long backupInterval = 10 * 1000;
         * Timer timer = new Timer();
         * timer.scheduleAtFixedRate(new TimerTask() {
         * @Override public void run() {
         * String text = editor.getContentAsSerialized();
         * SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
         * preferences.putString(String.format("backup-{0}",  new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date())), text);
         * preferences.apply();
         * }
         * }, 0, backupInterval);
         */
    }

    private fun insertMacro(): View {
        val view: View = layoutInflater.inflate(R.layout.layout_authored_by, null)
        val map: MutableMap<String, Any> =
            HashMap()
        map["author-name"] = "Alex Wong"
        map["date"] = "12 July 2018"
        editor!!.insertMacro("author-tag", view, map)
        return view
    }

    private fun colorHex(color: Int): String {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return String.format(Locale.getDefault(), "#%02X%02X%02X", r, g, b)
    }

    private fun render() {
        val x =
            "<h2 id=\"installation\" style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;color:#c00000;background-color:#333;text-align:center; margin-top: -80px !important;\">Installation</h2>" +
                    "<h3 id=\"requires-html5-doctype\" style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;color:#ff0000; margin-bottom: 8px; margin-right: 0px; margin-left: 0px;\">Requires HTML5 doctype</h3>" +
                    "<p style=\"font-size: 14px; color: rgb(104, 116, 127);\">Bootstrap uses certain HTML elements and CSS properties which require HTML5 doctype. Include&nbsp;<code style=\"font-size: 12.6px;\">&lt;!DOCTYPE html&gt;</code>&nbsp;in the beginning of all your projects.</p>" +
                    "<img src=\"http://www.scifibloggers.com/wp-content/uploads/TOR_2.jpg\" />" +
                    "<h2 id=\"integration\" style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin-top: -80px !important;\">Integration</h2>" +
                    "<p style=\"font-size: 14px; color: rgb(104, 116, 127);\">3rd parties available in django, rails, angular and so on.</p>" +
                    "<h3 id=\"django\" style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin-bottom: 8px; margin-right: 0px; margin-left: 0px;\">Django</h3>" +
                    "<p style=\"font-size: 14px; color: rgb(104, 116, 127);\">Handy update for your django admin page.</p>" +
                    "<ul style=\"color: rgb(51, 51, 51);\"><li style=\"font-size: 14px; color: #c00000;\">django-summernote</li><li style=\"font-size: 14px; color: rgb(104, 116, 127);\"><a href=\"https://pypi.python.org/pypi/django-summernote\" target=\"_blank\">summernote plugin for Django</a></li></ul>" +
                    "<h3 id=\"ruby-on-rails\" style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin-bottom: 8px; margin-right: 0px; margin-left: 0px;\">Ruby On Rails</h3>" +
                    "<p style=\"font-size: 14px; color: rgb(104, 116, 127);\">This gem was built to gemify the assets used in Summernote.</p>" +
                    "<ul style=\"color: rgb(51, 51, 51);\"><li style=\"font-size: 14px; color: rgb(104, 116, 127);\"><a href=\"https://github.com/summernote/summernote-rails\" target=\"_blank\">summernote-rails</a></li></ul>" +
                    "<h3 id=\"angularjs\" style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin-bottom: 8px; margin-right: 0px; margin-left: 0px;\">AngularJS</h3>" +
                    "<p style=\"font-size: 14px; color: rgb(104, 116, 127);\">AngularJS directive to Summernote.</p>" +
                    "<ul style=\"color: rgb(51, 51, 51);\"><li style=\"font-size: 14px; color: rgb(104, 116, 127);\"><a href=\"https://github.com/summernote/angular-summernote\">angular-summernote</a></li></ul>" +
                    "<h3 id=\"apache-wicket\" style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin-bottom: 8px; margin-right: 0px; margin-left: 0px;\">Apache Wicket</h3>" +
                    "<p style=\"font-size: 14px; color: rgb(104, 116, 127);\">Summernote widget for Wicket Bootstrap.</p>" +
                    "<ul style=\"color: rgb(51, 51, 51);\"><li style=\"font-size: 14px; color: rgb(104, 116, 127);\"><a href=\"http://wb-mgrigorov.rhcloud.com/summernote\" target=\"_blank\">demo</a></li><li style=\"font-size: 14px; color: rgb(104, 116, 127);\"><a href=\"https://github.com/l0rdn1kk0n/wicket-bootstrap/tree/4f97ca783f7279ca43f9e2ee790703161f59fa40/bootstrap-extensions/src/main/java/de/agilecoders/wicket/extensions/markup/html/bootstrap/editor\" target=\"_blank\">source code</a></li></ul>" +
                    "<h3 id=\"webpack\" style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin-bottom: 8px; margin-right: 0px; margin-left: 0px;\">Webpack</h3>" +
                    "<p style=\"font-size: 14px; color: rgb(104, 116, 127);\">Example about using summernote with webpack.</p>" +
                    "<ul style=\"color: rgb(51, 51, 51);\"><li style=\"font-size: 14px; color: rgb(104, 116, 127);\"><a href=\"https://github.com/hackerwins/summernote-webpack-example\" target=\"_blank\">summernote-webpack-example</a></li></ul>" +
                    "<h3 id=\"meteor\" style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin-bottom: 8px; margin-right: 0px; margin-left: 0px;\">Meteor</h3>" +
                    "<p style=\"font-size: 14px; color: rgb(104, 116, 127);\">Example about using summernote with meteor.</p>" +
                    "<ul style=\"color: rgb(51, 51, 51);\"><li style=\"font-size: 14px; color: rgb(104, 116, 127);\"><a href=\"https://github.com/hackerwins/summernote-meteor-example\" target=\"_blank\">summernote-meteor-example</a></li></ul>" +
                    "<p style=\"font-size: 14px; color: rgb(104, 116, 127);\"><br></p>"
        editor!!.render(x)
    }

    override fun onActivityResult(requestCode: Int,resultCode: Int, data: Intent?) {
        if (requestCode == editor!!.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                // Log.d(TAG, String.valueOf(bitmap));
                editor!!.insertImage(bitmap)
            } catch (e: IOException) {
                Toast.makeText(thisContext, e.message, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            Toast.makeText(thisContext, "Cancelled", Toast.LENGTH_SHORT).show()
            // editor.RestoreState();
        }
    }


//
//    override fun onPostCreate(savedInstanceState: Bundle?) {
//        super.onPostCreate(savedInstanceState)
//        setGhost(binding.root.findViewById<View>(R.id.btnRender) as Button)
//    }

    val headingTypeface: Map<Int, String>
        get() {
            val typefaceMap: MutableMap<Int, String> =
                HashMap()
            typefaceMap[Typeface.NORMAL] = "fonts/GreycliffCF-Bold.ttf"
            typefaceMap[Typeface.BOLD] = "fonts/GreycliffCF-Heavy.ttf"
            typefaceMap[Typeface.ITALIC] = "fonts/GreycliffCF-Heavy.ttf"
            typefaceMap[Typeface.BOLD_ITALIC] = "fonts/GreycliffCF-Bold.ttf"
            return typefaceMap
        }

    val contentface: Map<Int, String>
        get() {
            val typefaceMap: MutableMap<Int, String> =
                HashMap()
            typefaceMap[Typeface.NORMAL] = "fonts/Lato-Medium.ttf"
            typefaceMap[Typeface.BOLD] = "fonts/Lato-Bold.ttf"
            typefaceMap[Typeface.ITALIC] = "fonts/Lato-MediumItalic.ttf"
            typefaceMap[Typeface.BOLD_ITALIC] = "fonts/Lato-BoldItalic.ttf"
            return typefaceMap
        }





    //FULL SCREEN DIALOG
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return BottomSheetDialog(requireContext(), theme).apply {
////            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
////            behavior.peekHeight = PEEK_HEIGHT_AUTO
////            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
//            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
//        }
//    }
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
        fun setGhost(button: Button) {
            val radius = 4
            val background = GradientDrawable()
            background.shape = GradientDrawable.RECTANGLE
            background.setStroke(4, Color.WHITE)
            background.cornerRadius = radius.toFloat()
            button.setBackgroundDrawable(background)
        }


        @JvmStatic
        fun newInstance(questions: Questions) =
            FragmentAnswerQuestion().apply {
                arguments = Bundle().apply {
                    putParcelable(QUESTION, questions)
                }
            }
    }
}

