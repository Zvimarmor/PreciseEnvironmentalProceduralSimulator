package pepse.world;

/**
 * Observer interface for handling jump-related events.
 * Classes implementing this interface can react when the avatar jumps.
 */
public interface JumpObserver {
	/**
	 * Called when the avatar performs a jump.
	 */
	void onJump();
}
