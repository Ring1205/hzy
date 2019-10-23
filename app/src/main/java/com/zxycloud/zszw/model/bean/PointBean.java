package com.zxycloud.zszw.model.bean;

import com.zxycloud.common.base.BaseBean;

public class PointBean extends BaseBean {
        /**
         * layerImageX : 1
         * layerImageY : 1
         * placeLayerImage :
         */

        private double layerImageX;
        private double layerImageY;
        private String placeLayerImage;

        public double getLayerImageX() {
            return layerImageX;
        }

        public void setLayerImageX(int layerImageX) {
            this.layerImageX = layerImageX;
        }

        public double getLayerImageY() {
            return layerImageY;
        }

        public void setLayerImageY(int layerImageY) {
            this.layerImageY = layerImageY;
        }

        public String getPlaceLayerImage() {
            return placeLayerImage;
        }

        public void setPlaceLayerImage(String placeLayerImage) {
            this.placeLayerImage = placeLayerImage;
        }
}
