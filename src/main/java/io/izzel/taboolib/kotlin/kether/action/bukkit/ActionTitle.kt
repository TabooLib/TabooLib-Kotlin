package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.actions.LiteralAction
import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.action.ActionPass
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Features
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTitle(val title: ParsedAction<*>, val subTitle: ParsedAction<*>, val fadeIn: Int, val stay: Int, val fadeOut: Int) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(title).run<Any>().thenAccept { t ->
            context.newFrame(subTitle).run<Any>().thenAccept { s ->
                val viewer = (context.context() as ScriptContext).sender as? Player ?: throw RuntimeException("No player selected.")
                val title = t.toString().trimIndent().replace("@sender", viewer.name)
                val subTitle = s.toString().trimIndent().replace("@sender", viewer.name)
                TLocale.Display.sendTitle(viewer, title, subTitle, fadeIn, stay, fadeOut)
            }
        }
    }

    override fun toString(): String {
        return "ActionTitle(title=$title, subTitle=$subTitle, fadeIn=$fadeIn, stay=$stay, fadeOut=$fadeOut)"
    }

    companion object {

        @KetherParser(["title"])
        fun parser() = ScriptParser.parser {
            val title = it.next(ArgTypes.ACTION)
            it.mark()
            val subTitle = try {
                it.expect("subtitle")
                it.next(ArgTypes.ACTION)
            } catch (ignored: Exception) {
                it.reset()
                ParsedAction(ActionPass())
            }
            var fadeIn = 0
            var stay = 20
            var fadeOut = 0
            it.mark()
            try {
                it.expects("by", "with")
                fadeIn = it.nextInt()
                stay = it.nextInt()
                fadeOut = it.nextInt()
            } catch (ignored: Exception) {
                it.reset()
            }
            ActionTitle(title, subTitle, fadeIn, stay, fadeOut)
        }
    }
}