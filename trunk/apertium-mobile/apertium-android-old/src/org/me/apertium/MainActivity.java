/*
 * Copyright (C) 2009 Enrique Benimeli Bofarull <ebenimeli.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.me.apertium;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import engine.utils.ApertiumWS;

/**
 *
 * @author Enrique Benimeli Bofarull
 * @version 1.0
 */
public class MainActivity extends Activity {
    private boolean markUnk;
    private String[] language_codes;

    @Override
    /**
     *
     */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.translateButton);
        language_codes = this.getResources().getStringArray(R.array.language_codes);

        // 'Mark unknown words' checkbox
        CheckBox markUnknownCheckBox = (CheckBox) findViewById(R.id.markUnknown);
        markUnknownCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    markUnk = true;
                } else {
                    markUnk = false;
                }
            }
        });

        // Translation button
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String sourceText = getSourceText();
                String dir = getLanguageDir();

                try {
                    String[] langcode = dir.split("-");
                    String sl = langcode[0];
                    String tl = langcode[1];
                    String translation = translate_text(sl, tl, sourceText, markUnk);
                    showTranslation(translation);
                } catch (ArrayIndexOutOfBoundsException oobe) {
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     *
     * @return The source text
     */
    private final String getSourceText() {
        EditText et = (EditText) findViewById(R.id.sourceText);
        String text = et.getText().toString();
        return text;
    }

    /**
     *
     * @return The direction, i.e. es-ca, en-es
     */
    private final String getLanguageDir() {
        Spinner sp = (Spinner) findViewById(R.id.langpairs);
        int pos = sp.getSelectedItemPosition();
        String lc = language_codes[pos];
        return lc;
    }

    /**
     *
     * @param sl Source language
     * @param tl Target language
     * @param text Source text
     * @param markUnknown 'Mark unkown words' option
     * @return The translation
     */
    private String translate_text(String sl, String tl, String text, boolean markUnknown) {
        ApertiumWS translator = null;
        translator = new ApertiumWS(sl, tl, text);
        translator.setMarkUnknown(markUnk);

        String translation = translator.translate();
        return translation;
    }

    /**
     *
     * @param translation The translation
     */
    private final void showTranslation(String translation) {
        EditText tv = (EditText) findViewById(R.id.translationText);
        tv.setText(translation);
    }
}
