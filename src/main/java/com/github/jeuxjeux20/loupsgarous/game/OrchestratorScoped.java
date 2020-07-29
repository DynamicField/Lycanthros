package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.chat.LGChatOrchestrator;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhasesOrchestrator;
import com.github.jeuxjeux20.loupsgarous.phases.dusk.DuskPhase;
import com.google.inject.ScopeAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated classes are injected in an orchestrator scope, where {@link LGGameOrchestrator} is available
 * to inject. If this class is injected outside an orchestrator scope, an error will occur.
 * <p>
 * Places where dependencies are injected in an orchestrator scope include:
 * <ul>
 *     <li>Phases created in a {@link LGPhasesOrchestrator}</li>
 *     <li>Chat channels in a {@link LGChatOrchestrator}</li>
 *     <li>Dusk actions of a {@link DuskPhase}</li>
 *     <li>Everything related to descriptors (finders, processors, registries...)</li>
 * </ul>
 * <p>
 * You can manually run code in an orchestrator scope like this:
 * <blockquote><pre>
 * try (OrchestratorScope.Block block = orchestrator.scope()) {
 *     // Inject some stuff
 * }
 * </pre></blockquote>
 * <p>
 * By default, some bindings may be scoped using {@code OrchestratorScoped}. Check
 * the documentation of the module you're using for declaring bindings for
 * information about default scopes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@ScopeAnnotation
public @interface OrchestratorScoped {
}
