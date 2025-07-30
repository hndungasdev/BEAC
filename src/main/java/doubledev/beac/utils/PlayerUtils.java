package doubledev.beac.utils;

import doubledev.beac.BEAC;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerUtils {
    public static boolean wasLookingAtHitbox(Player player,
                                             LivingEntity entity,
                                             List<Location> historicalLocations) {
        Location eyeLocation = player.getEyeLocation();
        Vector eyePosition = eyeLocation.toVector();
        Vector lookDirection = eyeLocation.getDirection();

        // Tính khoảng cách tối đa dựa trên vị trí lịch sử gần nhất
        double maxDistance = historicalLocations.stream()
                .mapToDouble(loc -> loc.distance(eyeLocation))
                .min().orElse(0) + 1.0;

        BoundingBox baseBox = entity.getBoundingBox();
        double widthX = baseBox.getWidthX() / 2;
        double height = baseBox.getHeight();
        double widthZ = baseBox.getWidthZ() / 2;
//
//        // Debug: Hiển thị hướng nhìn của người chơi
//        drawDirectionLine(player.getWorld(), eyeLocation, lookDirection, maxDistance, Particle.CRIT);

        for (Location historicalLoc : historicalLocations) {
            Vector center = historicalLoc.toVector().add(new Vector(0, height / 2, 0));

            BoundingBox historicalBox = new BoundingBox(
                    center.getX() - widthX,
                    center.getY() - height / 2,
                    center.getZ() - widthZ,
                    center.getX() + widthX,
                    center.getY() + height / 2,
                    center.getZ() + widthZ
            );

            historicalBox.expand(0.3); // Mở rộng hitbox một chút để dễ trúng hơn

//            // Debug: Vẽ khung hitbox
//            drawBoundingBox(player.getWorld(), historicalBox, Particle.END_ROD);

            RayTraceResult hit = historicalBox.rayTrace(eyePosition, lookDirection, maxDistance);
            if (hit != null) {
//                // Debug: Điểm va chạm với hitbox (màu xanh lá)
//                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
//                        hit.getHitPosition().toLocation(player.getWorld()),
//                        10, 0, 0, 0, 0.05);

                double hitDistance = hit.getHitPosition().distance(eyePosition);

                // Kiểm tra vật cản
                RayTraceResult blockHit = eyeLocation.getWorld().rayTraceBlocks(
                        eyeLocation,
                        lookDirection,
                        hitDistance,
                        FluidCollisionMode.NEVER,
                        true);

                if (blockHit != null && blockHit.getHitBlock() != null) {
                    // Debug: Vật cản chặn (màu đỏ)
//                    player.getWorld().spawnParticle(Particle.LAVA,
//                            blockHit.getHitPosition().toLocation(player.getWorld()),
//                            15, 0, 0, 0, 0.05);
                }

                if (blockHit == null || blockHit.getHitBlock() == null || !blockHit.getHitBlock().getType().isOccluding()) {
//                    player.getWorld().spawnParticle(Particle.FIREWORK,
//                            hit.getHitPosition().toLocation(player.getWorld()),
//                            20, 0, 0, 0, 0.1);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Vẽ khung bao quanh bounding box
     */
    private static void drawBoundingBox(World world, BoundingBox box, Particle particle) {
        double step = 0.2; // Độ dày của đường viền
        Vector min = box.getMin();
        Vector max = box.getMax();

        // Vẽ 4 cạnh đáy
        drawLine(world, min.getX(), min.getY(), min.getZ(), max.getX(), min.getY(), min.getZ(), step, particle);
        drawLine(world, max.getX(), min.getY(), min.getZ(), max.getX(), min.getY(), max.getZ(), step, particle);
        drawLine(world, max.getX(), min.getY(), max.getZ(), min.getX(), min.getY(), max.getZ(), step, particle);
        drawLine(world, min.getX(), min.getY(), max.getZ(), min.getX(), min.getY(), min.getZ(), step, particle);

        // Vẽ 4 cạnh đỉnh
        drawLine(world, min.getX(), max.getY(), min.getZ(), max.getX(), max.getY(), min.getZ(), step, particle);
        drawLine(world, max.getX(), max.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), step, particle);
        drawLine(world, max.getX(), max.getY(), max.getZ(), min.getX(), max.getY(), max.getZ(), step, particle);
        drawLine(world, min.getX(), max.getY(), max.getZ(), min.getX(), max.getY(), min.getZ(), step, particle);

        // Vẽ 4 cạnh dọc
        drawLine(world, min.getX(), min.getY(), min.getZ(), min.getX(), max.getY(), min.getZ(), step, particle);
        drawLine(world, max.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), min.getZ(), step, particle);
        drawLine(world, max.getX(), min.getY(), max.getZ(), max.getX(), max.getY(), max.getZ(), step, particle);
        drawLine(world, min.getX(), min.getY(), max.getZ(), min.getX(), max.getY(), max.getZ(), step, particle);
    }

    /**
     * Vẽ đường thẳng giữa 2 điểm
     */
    private static void drawLine(World world, double x1, double y1, double z1,
                                 double x2, double y2, double z2,
                                 double step, Particle particle) {
        Vector direction = new Vector(x2 - x1, y2 - y1, z2 - z1);
        double length = direction.length();
        direction.normalize();

        for (double d = 0; d <= length; d += step) {
            Vector point = direction.clone().multiply(d).add(new Vector(x1, y1, z1));
            world.spawnParticle(particle, point.getX(), point.getY(), point.getZ(), 1, 0, 0, 0, 0);
        }
    }

    /**
     * Vẽ đường hướng nhìn của người chơi
     */
    private static void drawDirectionLine(World world, Location start, Vector direction,
                                          double length, Particle particle) {
        for (double d = 0; d <= length; d += 0.2) {
            Vector point = direction.clone().multiply(d).add(start.toVector());
            world.spawnParticle(particle, point.getX(), point.getY(), point.getZ(), 1, 0, 0, 0, 0);
        }
    }

    public static void drawParticleLine(World world, Vector from, Vector to, Particle particle, double step) {
        Vector direction = to.clone().subtract(from);
        double length = direction.length();
        direction.normalize();

        for (double d = 0; d <= length; d += step) {
            Vector point = from.clone().add(direction.clone().multiply(d));
            Location loc = new Location(world, point.getX(), point.getY(), point.getZ());

            world.spawnParticle(particle, loc, 1, 0, 0, 0, 0);
        }
    }

    public static double distanceToBox(Player player, LivingEntity entity, Vector pos, long ping) {
        BoundingBox origin = entity.getBoundingBox();

        double height = origin.getHeight();
        double widthX = origin.getWidthX();
        double widthZ = origin.getWidthZ();

        Vector adjusted = pos.clone().add(new Vector(0, height / 2, 0));

        BoundingBox box = BoundingBox.of(adjusted, widthX / 2, height / 2, widthZ / 2);

        if(Objects.requireNonNull(BEAC.getInstance().getServer().getRegionTPS(player.getLocation())).length == 0) return 0;

        double avgTPS = Arrays.stream(BEAC.getInstance().getServer().getRegionTPS(player.getLocation())).sum() / (BEAC.getInstance().getServer().getRegionTPS(player.getLocation()).length);

        Vector velocity = player.getVelocity();
        Vector eyePos = player.getEyeLocation().toVector().add(velocity.multiply((ping + 150L) / (1000D / avgTPS) + 1));

        System.out.println((ping + 150L) / (1000D / avgTPS));

        double closestX = MathUtils.clamp(eyePos.getX(), box.getMinX(), box.getMaxX());
        double closestY = MathUtils.clamp(eyePos.getY(), box.getMinY(), box.getMaxY());
        double closestZ = MathUtils.clamp(eyePos.getZ(), box.getMinZ(), box.getMaxZ());

        Vector closestPoint = new Vector(closestX, closestY, closestZ);

        drawParticleLine(player.getWorld(), eyePos, closestPoint, Particle.END_ROD, 0.2);

        return eyePos.distance(closestPoint);
    }
}