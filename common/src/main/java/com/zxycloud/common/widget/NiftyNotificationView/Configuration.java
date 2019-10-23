package com.zxycloud.common.widget.NiftyNotificationView;
/*
 * Copyright 2014 gitonway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.view.Gravity;

public class Configuration {

    public static final int ANIM_DURATION = 700;

    public static final int ANIM_DISPLAY_DURATION = 1500;

    final long animDuration;

    final long dispalyDuration;

    final int textColor;

    final int backgroundRes;

    final int viewHeight;

    final int iconBackgroundRes;

    final int textGravity;

    final int textLines;

    final int textPadding;

    final Object tag;

    final Builder.Margin margin;
    final Builder.Margin textMargin;

    private Configuration(final Builder builder) {
        this.animDuration = builder.animDuration;
        this.textColor = builder.textColor;
        this.dispalyDuration = builder.dispalyDuration;
        this.backgroundRes = builder.backgroundRes;
        this.textPadding = builder.textPadding;
        this.viewHeight = builder.viewHeight;
        this.iconBackgroundRes = builder.iconBackgroundColor;
        this.textGravity = builder.textGravity;
        this.textLines = builder.textLines;
        this.tag = builder.tag;
        margin = builder.margin;
        textMargin = builder.textMargin;
    }

    public static class Builder {

        private long animDuration;

        private long dispalyDuration;

        private int textColor;

        private int backgroundRes;

        private int textPadding;

        private int viewHeight;

        private int iconBackgroundColor;

        private int textGravity;

        private int textLines;

        private Object tag;

        private Margin margin;
        private Margin textMargin;

        public Builder() {
            animDuration = ANIM_DURATION;
            dispalyDuration = ANIM_DISPLAY_DURATION;
            textColor = android.R.color.black;
            backgroundRes = android.R.color.white;
            textPadding = 5;
            viewHeight = 48;
            iconBackgroundColor = android.R.color.white;
            textGravity = Gravity.CENTER;
            textLines = 2;
        }

        public Builder(final Configuration baseStyle) {
            animDuration = baseStyle.animDuration;
            textColor = baseStyle.textColor;
            backgroundRes = baseStyle.backgroundRes;
            textPadding = baseStyle.textPadding;
            viewHeight = baseStyle.viewHeight;
            iconBackgroundColor = baseStyle.iconBackgroundRes;
            textGravity = baseStyle.textGravity;
            textLines = baseStyle.textLines;
        }


        public Builder setAnimDuration(long animDuration) {
            this.animDuration = animDuration;
            return this;
        }

        public Builder setDispalyDuration(long dispalyDuration) {
            this.dispalyDuration = dispalyDuration;
            return this;
        }

        public Builder setTextColor(@ColorRes int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder setBackgroundColor(@DrawableRes int backgroundRes) {
            this.backgroundRes = backgroundRes;
            return this;
        }

        public Builder setTextPadding(int textPadding) {
            this.textPadding = textPadding;
            return this;
        }

        public Builder setViewHeight(int viewHeight) {
            this.viewHeight = viewHeight;
            return this;
        }

        public Builder setIconBackgroundRes(int iconBackgroundColor) {
            this.iconBackgroundColor = iconBackgroundColor;
            return this;
        }

        public Builder setTextGravity(int textGravity) {
            this.textGravity = textGravity;
            return this;
        }

        public Builder setTextLines(int textLines) {
            this.textLines = textLines;
            return this;
        }

        public Builder setMargin(Margin margin) {
            this.margin = margin;
            return this;
        }

        public Builder setTextMargin(Margin textMargin) {
            this.textMargin = textMargin;
            return this;
        }

        public Builder setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }

        public static class Margin {
            private int margin = - 1;

            private int marginTop = - 1;
            private int marginStart = - 1;
            private int marginEnd = - 1;
            private int marginBottom = - 1;

            public Margin(@IntRange(from = 0) int margin) {
                this.margin = margin;
            }

            public Margin(@IntRange(from = 0) int marginStart, @IntRange(from = 0) int marginTop, @IntRange(from = 0) int marginEnd, @IntRange(from = 0) int marginBottom) {
                this.marginStart = marginStart;
                this.marginTop = marginTop;
                this.marginBottom = marginBottom;
                this.marginEnd = marginEnd;
            }

            int getMarginTop() {
                return marginTop == - 1 ? margin == - 1 ? 0 : margin : marginTop;
            }

            int getMarginStart() {
                return marginStart == - 1 ? margin == - 1 ? 0 : margin : marginStart;
            }

            int getMarginEnd() {
                return marginEnd == - 1 ? margin == - 1 ? 0 : margin : marginEnd;
            }

            int getMarginBottom() {
                return marginBottom == - 1 ? margin == - 1 ? 0 : margin : marginBottom;
            }

            boolean isSet() {
                return margin * marginBottom * marginEnd * marginStart * marginTop != - 1;
            }
        }
    }
}
