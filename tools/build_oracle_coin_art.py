"""Model and render original square-hole casting coins, without fonts or stock art."""
import argparse
import math
from pathlib import Path
import sys

import bpy
from mathutils import Vector


parser = argparse.ArgumentParser()
parser.add_argument('--out', type=Path, required=True)
args = parser.parse_args(sys.argv[sys.argv.index('--') + 1:])
out = args.out.resolve()
out.mkdir(parents=True, exist_ok=True)
bpy.ops.object.select_all(action='SELECT')
bpy.ops.object.delete(use_global=False)
scene = bpy.context.scene


def material(name, color, roughness, metallic=1):
    mat = bpy.data.materials.new(name)
    mat.use_nodes = True
    shader = mat.node_tree.nodes.get('Principled BSDF')
    shader.inputs['Base Color'].default_value = (*color, 1)
    shader.inputs['Metallic'].default_value = metallic
    shader.inputs['Roughness'].default_value = roughness
    return mat


bronze = material('Brushed warm copper', (.52, .27, .085), .30)
gold = material('Polished raised brass', (.86, .57, .20), .23)
field = material('Satin sun field', (.64, .40, .13), .40)
patina = material('Deep oxidized bronze moon field', (.020, .065, .055), .48, .85)
silver = material('Pale electrum moon relief', (.70, .75, .65), .27)
groove = material('Recessed bronze engraving', (.12, .063, .027), .46)


def finish(obj, mat, bevel=0, smooth=False):
    obj.data.materials.append(mat)
    if bevel:
        mod = obj.modifiers.new('Rounded minted edges', 'BEVEL')
        mod.width, mod.segments = bevel, 3
        bpy.context.view_layer.objects.active = obj
        bpy.ops.object.modifier_apply(modifier=mod.name)
    for face in obj.data.polygons:
        face.use_smooth = smooth
    return obj


def annulus(name, radius, hole_half, bottom, top, mat, side=1):
    # Radial quads connect a true square opening to the circular outer edge.
    vertices, faces = [], []
    count = 192
    for z in (bottom, top):
        for inner in (False, True):
            for i in range(count):
                angle = math.tau * i / count
                x, y = math.cos(angle), math.sin(angle)
                r = hole_half / max(abs(x), abs(y)) if inner else radius
                vertices.append((side * r * x, r * y, side * z))
    for i in range(count):
        j = (i + 1) % count
        faces.extend([(i, j, count + j, count + i),
                      (2 * count + i, 3 * count + i, 3 * count + j, 2 * count + j),
                      (i, 2 * count + i, 2 * count + j, j),
                      (count + i, count + j, 3 * count + j, 3 * count + i)])
    mesh = bpy.data.meshes.new(name)
    mesh.from_pydata(vertices, [], faces)
    mesh.update()
    obj = bpy.data.objects.new(name, mesh)
    scene.collection.objects.link(obj)
    # The opposite side reverses winding; consistently recalculate outward normals.
    bpy.context.view_layer.objects.active = obj
    obj.select_set(True)
    bpy.ops.object.mode_set(mode='EDIT')
    bpy.ops.mesh.select_all(action='SELECT')
    bpy.ops.mesh.normals_make_consistent(inside=False)
    bpy.ops.object.mode_set(mode='OBJECT')
    obj.select_set(False)
    return finish(obj, mat, .006)


def curve(name, points, mat, width=.008, side=1, closed=False):
    data = bpy.data.curves.new(name, 'CURVE')
    data.dimensions, data.resolution_u = '3D', 2
    data.bevel_depth, data.bevel_resolution = width, 3
    spline = data.splines.new('POLY')
    spline.points.add(len(points) - 1)
    for point, (x, y, z) in zip(spline.points, points):
        point.co = (side * x, y, side * z, 1)
    spline.use_cyclic_u = closed
    obj = bpy.data.objects.new(name, data)
    scene.collection.objects.link(obj)
    obj.data.materials.append(mat)
    return obj


def ring(radius, z, mat, width=.009, side=1):
    return curve('Turned circular rim', [(radius * math.cos(i * math.tau / 192),
                 radius * math.sin(i * math.tau / 192), z) for i in range(192)],
                 mat, width, side, True)


def disc(name, x, y, z, radius, mat, side=1, depth=.012):
    bpy.ops.mesh.primitive_cylinder_add(vertices=64, radius=radius, depth=depth,
                                       location=(side * x, y, side * z))
    obj = bpy.context.object
    obj.name = name
    return finish(obj, mat, .004)


def bar(x, y, length, mat, side):
    bpy.ops.mesh.primitive_cube_add(size=1, location=(side * x, y, side * .087))
    obj = bpy.context.object
    obj.name = 'Raised trigram stroke'
    obj.scale = (length, .035, .020)
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    finish(obj, mat, .007)


annulus('Square-hole copper body', 1, .19, -.065, .065, bronze)
for side in (1, -1):
    light = gold if side == 1 else silver
    annulus('Recessed sun or moon field', .875, .207, .065, .073,
            field if side == 1 else patina, side)
    for radius, z, width, mat in [(.977, .067, .018, gold), (.913, .073, .009, groove),
                                 (.872, .077, .013, gold), (.837, .077, .004, light)]:
        ring(radius, z, mat, width, side)
    for half, width, mat in [(.200, .010, gold), (.227, .004, light)]:
        curve('Polished square opening bevel', [(-half, -half, .080), (half, -half, .080),
              (half, half, .080), (-half, half, .080)], mat, width, side, True)
    for i in range(72):
        theta = math.tau * i / 72
        disc('Beaded perimeter', .944 * math.cos(theta), .944 * math.sin(theta),
             .078, .009, light, side, .008)
    # Original symmetrical cloud relief leaves broad readable negative space.
    for mirror in (-1, 1):
        for vertical in (-1, 1):
            points = []
            for i in range(64):
                t = i / 63
                a, r = t * math.pi * 2.5, .11 * (1 - t) + .009
                points.append((mirror * (.56 + math.cos(a) * r),
                               vertical * (.13 + math.sin(a) * r), .085))
            curve('Ruyi cloud scroll', points, light, .009, side)
        curve('Cloud connecting stem', [(mirror * (.48 + .045 * math.cos(i * math.pi / 32)),
              -.30 + i * .60 / 32, .084) for i in range(33)], gold, .006, side)
    for row in range(3):
        y = -.46 - row * .075
        if side == 1:
            bar(0, y, .42, light, side)
        else:
            bar(-.14, y, .15, light, side)
            bar(.14, y, .15, light, side)

    if side == 1:
        disc('Recessed solar halo', 0, .54, .083, .124, groove, side, .012)
        disc('Raised solar disc', 0, .54, .097, .092, gold, side, .025)
        for i in range(12):
            a = math.tau * i / 12
            curve('Twelve sun rays', [(r * math.cos(a), .54 + r * math.sin(a), .088)
                  for r in (.151, .220 if i % 2 == 0 else .190)], gold, .012, side)
    else:
        # Closed crescent silhouette, extruded as real geometry.
        points = []
        for i in range(65):
            a = math.radians(64 + i * 232 / 64)
            points.append((.145 * math.cos(a), .54 + .145 * math.sin(a), .087))
        for i in range(65):
            t = i / 64
            points.append((.063 - .123 * math.sin(math.pi * t),
                           .54 - .130 + .260 * t, .087))
        crescent = curve('Raised lunar crescent', points, silver, .004, side, True)
        crescent.data.dimensions, crescent.data.fill_mode = '2D', 'BOTH'
        crescent.data.extrude = .010
        # Curves in 2D use local XY; keep the physical face placement on the object.
        for point in crescent.data.splines[0].points:
            point.co.z = 0
        crescent.location.z = side * .087
        for x, y, radius in [(.18, .63, .018), (.20, .46, .011), (-.14, .72, .010)]:
            disc('Lunar stars', x, y, .087, radius, gold, side, .012)

scene.world.use_nodes = True
world = scene.world.node_tree.nodes.get('Background')
world.inputs['Color'].default_value = (.67, .73, .82, 1)
world.inputs['Strength'].default_value = .65
for side in (1, -1):
    for location, energy, size in [((-3, 3, side * 5), 620, 4),
                                    ((3, -1, side * 4), 420, 3),
                                    ((1, 4, side * 2), 240, 2)]:
        bpy.ops.object.light_add(type='AREA', location=location)
        obj = bpy.context.object
        obj.data.energy, obj.data.size = energy, size
        obj.rotation_euler = (-obj.location).to_track_quat('-Z', 'Y').to_euler()
bpy.ops.object.camera_add(location=(0, 0, 6))
camera = bpy.context.object
camera.data.type, camera.data.ortho_scale = 'ORTHO', 2.08
scene.camera = camera
scene.render.engine = 'CYCLES'
scene.cycles.samples, scene.cycles.use_denoising = 48, True
scene.render.resolution_x, scene.render.resolution_y = 512, 512
scene.render.resolution_percentage = 100
scene.render.film_transparent = True
scene.render.image_settings.file_format = 'PNG'
scene.render.image_settings.color_mode = 'RGBA'
scene.view_settings.view_transform = 'AgX'
for side, name in [(1, 'oracle_coin_yang'), (-1, 'oracle_coin_yin')]:
    camera.location = (0, 0, side * 6)
    camera.rotation_euler = (-camera.location).to_track_quat('-Z', 'Y').to_euler()
    scene.render.filepath = str(out / (name + '.png'))
    bpy.ops.render.render(write_still=True)
scene.render.filepath = '//oracle_coin_yang.png'
bpy.ops.wm.save_as_mainfile(filepath=str(out / 'oracle_coin.blend'), compress=True)
print('ORACLE_COIN_ART: two 512px transparent faces; original sun/moon relief; no fonts or external images')
