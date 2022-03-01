package kr.kuvh.linebot.altcoin.bot.controller;

import com.linecorp.bot.model.message.ImageMessage;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import kr.kuvh.linebot.altcoin.bot.common.ExchangeEnum;
import kr.kuvh.linebot.altcoin.bot.util.TimeSeriesUtil;
import kr.kuvh.linebot.annotation.ChatCommand;
import kr.kuvh.linebot.annotation.ChatParam;
import kr.kuvh.linebot.util.Time;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Controller
public class ChartController {

    @ChatCommand(command = "차트 currencyPair", help = "차트 <화폐짝 예)BTC_ETH>")
    public ImageMessage showChart(@ChatParam("currencyPair") String currencyPair) {
        String uri = getChartImageUri(currencyPair);
        return new ImageMessage(uri, uri);
    }

    private String getChartImageUri(String currencyPair) {
        TimeSeries series = null;
        try {
            series = TimeSeriesUtil.getTimeSeries(ExchangeEnum.BITTREX, currencyPair, 48, 30);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Creating the OHLC dataset
         */
        OHLCDataset ohlcDataset = createOHLCDataset(series);

        /**
         * Creating the additional dataset
         */
        TimeSeriesCollection xyDataset = createAdditionalDataset(series);

        /**
         * Creating the chart
         */
        JFreeChart chart = ChartFactory.createCandlestickChart(
                currencyPair,
                "Time",
                "BTC",
                ohlcDataset,
                true);
        // Candlestick rendering
        CandlestickRenderer renderer = new CandlestickRenderer();
        renderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(renderer);
        // Additional dataset
        int index = 1;
        plot.setDataset(index, xyDataset);
        plot.mapDatasetToRangeAxis(index, 0);
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
        renderer2.setSeriesPaint(index, Color.blue);
        plot.setRenderer(index, renderer2);
        // Misc
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setBackgroundPaint(Color.white);
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        numberAxis.setAutoRangeIncludesZero(false);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        String filename = Time.getCurrentTimestamp() + "_chart.png";
        File chartDir = new File("static");
        if(!chartDir.exists()) {
            chartDir.mkdir();
        }
        File chartImgFile = new File("static/" + filename);

        try {
            FileOutputStream fos = new FileOutputStream(chartImgFile);
            ChartUtilities.writeChartAsPNG(fos, chart, 740, 300);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "https://linebot.wiselight.kr/static/" + filename;
    }

    private static OHLCDataset createOHLCDataset(TimeSeries series) {
        final int nbTicks = series.getTickCount();

        Date[] dates = new Date[nbTicks];
        double[] opens = new double[nbTicks];
        double[] highs = new double[nbTicks];
        double[] lows = new double[nbTicks];
        double[] closes = new double[nbTicks];
        double[] volumes = new double[nbTicks];

        for (int i = 0; i < nbTicks; i++) {
            Tick tick = series.getTick(i);
            dates[i] = new Date(tick.getEndTime().toEpochSecond() * 1000);
            opens[i] = tick.getOpenPrice().toDouble();
            highs[i] = tick.getMaxPrice().toDouble();
            lows[i] = tick.getMinPrice().toDouble();
            closes[i] = tick.getClosePrice().toDouble();
            volumes[i] = tick.getVolume().toDouble();
        }

        OHLCDataset dataset = new DefaultHighLowDataset("btc", dates, highs, lows, opens, closes, volumes);

        return dataset;
    }

    private static TimeSeriesCollection createAdditionalDataset(TimeSeries series) {
        ClosePriceIndicator indicator = new ClosePriceIndicator(series);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries("Btc price");
        for (int i = 0; i < series.getTickCount(); i++) {
            Tick tick = series.getTick(i);
            chartTimeSeries.add(new Second(new Date(tick.getEndTime().toEpochSecond() * 1000)), indicator.getValue(i).toDouble());
        }
        dataset.addSeries(chartTimeSeries);
        return dataset;
    }
}
