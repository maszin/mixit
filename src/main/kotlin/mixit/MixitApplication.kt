package mixit

import com.samskivert.mustache.Mustache
import mixit.repository.EventRepository
import mixit.repository.PostRepository
import mixit.repository.TalkRepository
import mixit.repository.UserRepository
import mixit.util.MarkdownConverter
import mixit.util.MixitWebFilter
import mixit.util.customizeModel
import mixit.util.run
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.result.view.mustache.MustacheResourceTemplateLoader
import org.springframework.web.reactive.result.view.mustache.MustacheViewResolver

@SpringBootApplication
@EnableConfigurationProperties(MixitProperties::class)
class MixitApplication {

    @Bean
    fun viewResolver(messageSource: MessageSource) = MustacheViewResolver().apply {
        val prefix = "classpath:/templates/"
        val suffix = ".mustache"
        val loader = MustacheResourceTemplateLoader(prefix, suffix)
        setPrefix(prefix)
        setSuffix(suffix)
        setCompiler(Mustache.compiler().escapeHTML(false).withLoader(loader))
        setModelCustomizer({ model, exchange -> customizeModel(model, exchange, messageSource) })
    }

    @Bean
    fun filter(mixitProperties: MixitProperties) = MixitWebFilter(mixitProperties.baseUri!!)

    @Bean
    fun markdownConverter() = MarkdownConverter()

    @Bean
    fun dataInitializer(userRepository: UserRepository, eventRepository: EventRepository,
                        talkRepository: TalkRepository, postRepository: PostRepository) = ApplicationRunner {
        userRepository.initData()
        eventRepository.initData()
        talkRepository.initData()
        postRepository.initData()
    }
}

fun main(args: Array<String>) {
    run(MixitApplication::class, *args)
}