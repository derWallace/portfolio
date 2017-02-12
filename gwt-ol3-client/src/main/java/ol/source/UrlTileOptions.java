package ol.source;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Abstract options für URL based tiles.
 * 
 * @author Tino Desjardins
 *
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class UrlTileOptions extends TileOptions {
    
    /**
     * Optional function to load a tile given a URL. The default is
     * 
     * function(imageTile, src) { imageTile.getImage().src = src; };
     */
    @JsProperty
    public native void setTileLoadFunction(TileLoadFunction callback);

    @JsProperty
    public native TileLoadFunction getTileLoadFunction();
    
    /**
     * Optional function to get tile URL given a tile coordinate and the
     * projection. Required if url or urls are not provided.
     */
    @JsProperty
    public native void setTileUrlFunction(TileUrlFunction callback);

    @JsProperty
    public native TileUrlFunction getTileUrlFunction();
    
    /**
     * Return the URLs used for this source. When a tileUrlFunction is used
     * instead of url or urls, null will be returned.
     * 
     * @return {!Array.<string>|null} URLs.
     */
    @JsProperty
    public native String[] getUrls();

    @JsProperty	
  	public native String getUrl();

    /**
     * Set the URL to use for requests.
     * 
     * @param url
     *            URL.
     */
    @JsProperty
    public native void setUrl(String url);

    /**
     * Set the URLs to use for requests.
     * 
     * @param urls
     *            URLs.
     */
    @JsProperty
    public native void setUrls(String[] urls);

}