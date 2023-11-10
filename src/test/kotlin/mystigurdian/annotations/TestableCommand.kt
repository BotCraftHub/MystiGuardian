package mystigurdian.annotations

@Target(AnnotationTarget.CLASS)
/**
 * This annotation is used to mark a slash-command as explicitly testable.
 *
 * Indicates that the command has been altered to be testable, and that it should be tested.
 */
annotation class TestableCommand
