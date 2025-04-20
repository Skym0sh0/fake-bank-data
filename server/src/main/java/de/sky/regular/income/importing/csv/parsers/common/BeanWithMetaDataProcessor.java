package de.sky.regular.income.importing.csv.parsers.common;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.CompositeRowProcessor;

import java.util.ArrayList;
import java.util.List;

public class BeanWithMetaDataProcessor<T> extends CompositeRowProcessor {
    private final RawMetaDataProcessor metaDataProcessor;
    private final BeanListProcessor<T> beanProcessor;

    private BeanWithMetaDataProcessor(RawMetaDataProcessor metaDataProcessor, BeanListProcessor<T> beanProcessor) {
        super(metaDataProcessor, beanProcessor);

        this.metaDataProcessor = metaDataProcessor;
        this.beanProcessor = beanProcessor;
    }

    public BeanWithMetaDataProcessor(Class<T> beanType, long lineOffset) {
        this(new RawMetaDataProcessor(lineOffset), new BeanListProcessor<>(beanType, 1000));
    }

    public BeanWithMetaDataProcessor(Class<T> beanType) {
        this(beanType, 0);
    }

    public List<CsvRecordWithMetadata<T>> getRows() {
        var beans = beanProcessor.getBeans();
        var metadata = metaDataProcessor.getRows();

        var list = new ArrayList<CsvRecordWithMetadata<T>>(beans.size());
        for (int i = 0; i < metadata.size(); i++) {
            var bean = beans.get(i);
            var meta = metadata.get(i);

            list.add(new CsvRecordWithMetadata<>(bean, meta));
        }

        return list;
    }
}
