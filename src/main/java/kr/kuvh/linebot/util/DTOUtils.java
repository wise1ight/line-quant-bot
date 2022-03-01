package kr.kuvh.linebot.util;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

public final class DTOUtils {

    private static final ModelMapper instance = new ModelMapper();

    private DTOUtils() {

    }

    public static <S, T> T map(S source, Class<T> destination) {
        return instance.map(source, destination);
    }

    public static <S, T> void mapTo(S source, T destination) {
        instance.map(source, destination);
    }

    public static <S, T> List<T> mapList(List<S> source, Class<T> destination) {
        List<T> list = new ArrayList<>();
        for (S s : source) {
            T target = instance.map(s, destination);
            list.add(target);
        }

        return list;
    }

    public static <S, T> Page<T> mapPage(Page<S> source, Class<T> destination) {
        List<S> sourceList = source.getContent();

        List<T> list = new ArrayList<>();
        for (S s : sourceList) {
            T target = instance.map(s, destination);
            list.add(target);
        }

        return new PageImpl<T>(list, new PageRequest(source.getNumber(), source.getSize(), source.getSort()),
                source.getTotalElements());
    }
}
