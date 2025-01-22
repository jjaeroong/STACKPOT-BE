package stackpot.stackpot.converter;

import lombok.Builder;
import org.springframework.stereotype.Component;
import stackpot.stackpot.domain.Feed;
import stackpot.stackpot.web.dto.FeedRequestDto;
import stackpot.stackpot.web.dto.FeedResponseDto;

import java.util.List;

@Component
public class FeedConverterImpl implements FeedConverter{

    @Override
    public FeedResponseDto.FeedDto feedDto(Feed feed, int popularity, int likeCount) {
        return FeedResponseDto.FeedDto.builder()
                .id(feed.getFeedId())
                .writer(feed.getUser().getNickname())
//                .category(feed.getMainPart())
                .title(feed.getTitle())
                .content(feed.getContent())
                .popularity(popularity)
                .likeCount(likeCount)
                .createdAt(feed.getCreatedAt())
                .build();
    }

    @Override
    public Feed toFeed(FeedRequestDto.createDto request) {
        return Feed.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .visibility(request.getVisibility())
                .build();
    }
}
