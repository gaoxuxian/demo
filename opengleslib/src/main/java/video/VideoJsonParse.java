package video;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import util.ThreadUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/13.
 */
public class VideoJsonParse
{
    public static void parse(final Context context, final String path, final CB<VideoBean> callback)
    {
        ThreadUtil.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AssetManager assets = context.getAssets();
                InputStream input = null;
                try
                {
                    input = assets.open(path);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                if (input != null)
                {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder stringBuffer = new StringBuilder();
                    String line = null;
                    try
                    {
                        while ((line = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(line).append("\n");
                        }

                        String data = stringBuffer.toString();

                        if (!TextUtils.isEmpty(data))
                        {
                            VideoBean videoBean = readBean(new JSONArray(data));

                            if (callback != null)
                            {
                                callback.onSucceed(videoBean);
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            input.close();
                        }
                        catch (Throwable e)
                        {
                            e.printStackTrace();
                        }

                        try
                        {
                            bufferedReader.close();
                        }
                        catch (Throwable e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private static VideoBean readBean(JSONArray jsonArray) throws JSONException
    {
        VideoBean out = null;

        if (jsonArray != null)
        {
            out = new VideoBean();

            int length = jsonArray.length();

            out.frame = new VideoBean.VideoFrame[length];

            // 解帧
            for (int i = 0; i < length; i++)
            {
                VideoBean.VideoFrame frame = new VideoBean.VideoFrame();

                Object object = jsonArray.get(i);

                if (object instanceof JSONArray)
                {
                    JSONArray array = (JSONArray) object;

                    int size = array.length();

                    frame.layers = new VideoBean.VideoFrameLayer[size];

                    // 解层
                    for (int j = 0; j < size; j++)
                    {
                        VideoBean.VideoFrameLayer layer = new VideoBean.VideoFrameLayer();

                        Object object2 = array.get(j);

                        if (object2 instanceof JSONObject)
                        {
                            JSONObject jsonObject = (JSONObject) object2;

                            if (jsonObject.has("vertexPots"))
                            {
                                JSONArray vertexPots = jsonObject.getJSONArray("vertexPots");
                                int vertexPotsLen = vertexPots.length();
                                layer.vertexPots = new float[vertexPotsLen];
                                float x = 0;
                                float y = 0;
                                for (int k = 0; k < vertexPotsLen; k++)
                                {
                                    double v = vertexPots.getDouble(k);
                                    if (k == 0)
                                    {
                                        x = (float) v;
                                    }
                                    else if (k == 1)
                                    {
                                        y = (float) v;
                                    }
                                    else
                                    {
                                        layer.vertexPots[k - 2] = (float) v;
                                    }
                                }

                                layer.vertexPots[vertexPotsLen - 2] = x;
                                layer.vertexPots[vertexPotsLen - 1] = y;
                            }

                            if (jsonObject.has("intAlpha"))
                            {
                                layer.intAlpha = jsonObject.getInt("intAlpha");
                            }

                            if (jsonObject.has("frameType"))
                            {
                                layer.frameType = jsonObject.getInt("frameType");
                            }

                            if (jsonObject.has("scaleX"))
                            {
                                layer.scaleX = (float) jsonObject.getDouble("scaleX");
                            }

                            if (jsonObject.has("scaleY"))
                            {
                                layer.scaleY = (float) jsonObject.getDouble("scaleY");
                            }

                            if (jsonObject.has("rolate"))
                            {
                                layer.rotate = (float) jsonObject.getDouble("rolate");
                            }

                            frame.layers[j] = layer;
                        }
                    }

                    out.frame[i] = frame;
                }
            }
        }

        return out;
    }

    public interface CB<T>
    {
        void onSucceed(T data);
    }
}
