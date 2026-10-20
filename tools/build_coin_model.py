"""Mint an original astrolabe coin; all ornament is geometry, not decals."""
import argparse
import math
from pathlib import Path
import sys
import bpy
from mathutils import Matrix, Vector

parser = argparse.ArgumentParser()
parser.add_argument('--out', type=Path, required=True)
parser.add_argument('--font', type=Path, default=Path(__file__).resolve().parents[1] /
                    'art' / 'coin' / 'fonts' / 'CinzelDecorative-Bold.ttf')
args = parser.parse_args(sys.argv[sys.argv.index('--') + 1:])
out = args.out.resolve()
out.mkdir(parents=True, exist_ok=True)
bpy.ops.object.select_all(action='SELECT')
bpy.ops.object.delete(use_global=False)
scene = bpy.context.scene
scene.name = 'Original astrolabe and cartouche coin'
font = bpy.data.fonts.load(str(args.font.resolve()))
parts = []


def metal(name, color, roughness):
    material = bpy.data.materials.new(name)
    material.use_nodes = True
    node = material.node_tree.nodes.get('Principled BSDF')
    node.inputs['Base Color'].default_value = (*color, 1)
    node.inputs['Metallic'].default_value = 1
    node.inputs['Roughness'].default_value = roughness
    return material


gold = metal('Satin champagne gold', (.64, .44, .20), .29)
silver = metal('Frosted nickel field', (.57, .62, .66), .38)
bright = metal('Mirror silver engraving', (.88, .91, .93), .19)
shadow = metal('Antique bronze recess', (.22, .16, .09), .34)
polish = metal('Polished gold relief', (.83, .63, .31), .20)


def face_point(point, side):
    x, y, z = point
    return side * x, y, side * z


def finish(obj, material, bevel=0, smooth=False):
    obj.data.materials.append(material)
    if bevel:
        modifier = obj.modifiers.new('Minted bevel', 'BEVEL')
        modifier.width = bevel
        modifier.segments = 2
        bpy.context.view_layer.objects.active = obj
        bpy.ops.object.modifier_apply(modifier=modifier.name)
    for polygon in obj.data.polygons:
        polygon.use_smooth = smooth or len(polygon.vertices) == 4
    parts.append(obj)
    return obj


def mesh_part(name, vertices, faces, material, side=1, smooth=False):
    mesh = bpy.data.meshes.new(name)
    mesh.from_pydata([face_point(v, side) for v in vertices], [], faces)
    mesh.update()
    obj = bpy.data.objects.new(name, mesh)
    scene.collection.objects.link(obj)
    return finish(obj, material, smooth=smooth)


def cylinder(name, radius, depth, material, z=0, side=1, oval=None):
    bpy.ops.mesh.primitive_cylinder_add(vertices=96, radius=radius, depth=depth,
                                       location=face_point((0, 0, z), side))
    obj = bpy.context.object
    obj.name = name
    if oval:
        obj.scale.x, obj.scale.y = oval
        bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    return finish(obj, material, bevel=.004)


def ridge(name, points, material, width=.003, height=.003, side=1, closed=False):
    count = len(points)
    vertices = []
    for i, point in enumerate(points):
        before = Vector(points[(i - 1) % count] if closed or i else points[0])
        after = Vector(points[(i + 1) % count] if closed or i < count - 1 else points[-1])
        tangent = (after - before).normalized()
        normal = Vector((-tangent.y, tangent.x, 0)).normalized()
        p = Vector(point)
        vertices.extend([tuple(p - normal * width), tuple(p + Vector((0, 0, height))),
                         tuple(p + normal * width)])
    faces = []
    for i in range(count if closed else count - 1):
        a, b = i * 3, ((i + 1) % count) * 3
        faces.extend([(a, b, b + 1, a + 1), (a + 1, b + 1, b + 2, a + 2)])
    return mesh_part(name, vertices, faces, material, side, smooth=True)


def ring(radius, z, material, side=1, width=.003, height=.003, oval=(1, 1)):
    points = [(radius * math.cos(t) * oval[0], radius * math.sin(t) * oval[1], z)
              for t in [i * math.tau / 128 for i in range(128)]]
    return ridge('Engine-turned border', points, material, width, height, side, closed=True)


def bead(x, y, z, radius, material, side=1):
    bpy.ops.mesh.primitive_ico_sphere_add(subdivisions=1, radius=radius,
                                         location=face_point((x, y, z), side))
    obj = bpy.context.object
    obj.name = 'Mint pearl'
    obj.scale.z = .65
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    return finish(obj, material, smooth=True)


def leaf(start, end, width, lift, side=-1):
    direction = Vector((end[0] - start[0], end[1] - start[1], 0)).normalized()
    normal = Vector((-direction.y, direction.x, 0))
    vertices, faces, vein = [], [], []
    for row in range(9):
        u = row / 8
        center = Vector((start[0] * (1 - u) + end[0] * u,
                         start[1] * (1 - u) + end[1] * u, .077))
        envelope = math.sin(math.pi * u) ** .75
        for column in range(5):
            v = (column - 2) / 2
            p = center + normal * (width * envelope * v)
            p.z += lift * envelope * (1 - v * v)
            vertices.append(tuple(p))
        vein.append(tuple(center + Vector((0, 0, lift * envelope + .001))))
    for row in range(8):
        for column in range(4):
            a = row * 5 + column
            faces.append((a, a + 5, a + 6, a + 1))
    mesh_part('Acanthus leaf relief', vertices, faces, gold, side, smooth=True)
    ridge('Acanthus central vein', vein, bright, .0018, .0015, side)


def text_mesh(value, x, y, height, side=1, rotation=0, z=.078, material=polish, width=None):
    bpy.ops.object.text_add()
    obj = bpy.context.object
    obj.name = 'Mint inscription ' + value
    obj.data.font = font
    obj.data.body = value
    obj.data.size = 1
    obj.data.align_x = 'CENTER'
    obj.data.align_y = 'CENTER'
    obj.data.extrude = .006
    obj.data.bevel_depth = .0015
    obj.data.bevel_resolution = 0
    obj.data.resolution_u = 2
    bpy.ops.object.convert(target='MESH')
    low = Vector(tuple(min(v.co[i] for v in obj.data.vertices) for i in range(3)))
    high = Vector(tuple(max(v.co[i] for v in obj.data.vertices) for i in range(3)))
    scale = height / max(high.y - low.y, .001)
    if width:
        scale = min(scale, width / max(high.x - low.x, .001))
    for vertex in obj.data.vertices:
        vertex.co.x = (vertex.co.x - (high.x + low.x) / 2) * scale
        vertex.co.y = (vertex.co.y - (high.y + low.y) / 2) * scale
        vertex.co.z = (vertex.co.z - low.z) * min(scale, 1.0)
    basis = Matrix.Rotation(math.pi if side < 0 else 0, 4, 'Y')
    obj.matrix_world = Matrix.Translation(face_point((x, y, z), side)) @ basis @ Matrix.Rotation(rotation, 4, 'Z')
    return finish(obj, material)


def arc_text(value, side=1, bottom=False):
    span = max(.22, (len(value) - 1) * .14)
    for i, character in enumerate(value):
        if character == ' ':
            continue
        t = -span / 2 + span * i / max(len(value) - 1, 1)
        theta = math.pi - t if bottom else t
        text_mesh(character, .70 * math.sin(theta), .70 * math.cos(theta), .072,
                  side, rotation=t if bottom else -t)


cylinder('Bimetal milled body', 1, .13, gold)
cylinder('Frosted silver field', .808, .14, silver)
for side in (-1, 1):
    for r, z, w, material in [(.986, .068, .006, bright), (.932, .071, .003, polish),
                              (.815, .072, .006, polish), (.768, .073, .002, bright)]:
        ring(r, z, material, side, w, w)
    for i in range(96):
        t = i * math.tau / 96
        bead(.960 * math.cos(t), .960 * math.sin(t), .071, .008, polish, side)
    for phase in (0, math.pi):
        points = []
        for i in range(320):
            t = i * math.tau / 320
            radius = .875 + .024 * math.cos(24 * t + phase)
            points.append((radius * math.cos(t), radius * math.sin(t), .072))
        ridge('Interlaced guilloche', points, bright if phase == 0 else polish,
              width=.0025, height=.0025, side=side, closed=True)
    for i in range(64):
        t = i * math.tau / 64
        if abs(math.cos(t)) < .45:
            continue
        ridge('Hairline radial engraving', [(r * math.cos(t), r * math.sin(t), .071)
              for r in (.714, .742)], bright, .001, .0009, side)

for i in range(144):
    t = i * math.tau / 144
    bpy.ops.mesh.primitive_cube_add(size=1, location=(.998 * math.cos(t), .998 * math.sin(t), 0))
    obj = bpy.context.object
    obj.name = 'Milled edge tooth'
    obj.scale = (.011, .011, .084)
    obj.rotation_euler.z = t
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    finish(obj, shadow)

# Pattern face: faceted compass rose, orbital dial and eight engraved volutes.
ring(.602, .074, polish, width=.003, height=.004)
ring(.628, .072, bright, width=.0015, height=.002)
for i in range(48):
    t = i * math.tau / 48
    radius = .565 if i % 6 == 0 else .586
    ridge('Astrolabe dial graduation', [(r * math.cos(t), r * math.sin(t), .074)
          for r in (radius, .606)], polish, .002 if i % 6 == 0 else .001, .002)
for i in range(8):
    theta = i * math.pi / 4
    radius = .546 if i % 2 == 0 else .426
    def polar(r, a, z):
        return r * math.cos(a), r * math.sin(a), z
    vertices = [polar(.16, theta - .30, .078), polar(radius, theta, .085),
                polar(.16, theta + .30, .078), polar(.048, theta, .082),
                polar(.20, theta, .126)]
    mesh_part('Compass rose light facet', vertices, [(0, 1, 4), (3, 0, 4)], polish)
    mesh_part('Compass rose silver facet', vertices, [(1, 2, 4), (2, 3, 4)], bright)
    theta += math.pi / 8
    center = Vector((.402 * math.cos(theta), .402 * math.sin(theta), .078))
    points = []
    for j in range(42):
        u = j / 41
        angle = theta + u * math.tau * 1.1
        r = .091 * (1 - .88 * u)
        points.append(tuple(center + Vector((r * math.cos(angle), r * math.sin(angle), 0))))
    ridge('Celestial volute', points, polish, .0032, .004)
ring(.092, .126, polish, width=.006, height=.004)
bead(0, 0, .125, .050, bright)
arc_text('ZHIFOU')
arc_text('FORTUNE', bottom=True)

# Number face: a classical serif numeral inside a fluted oval cartouche.
cylinder('Oval medallion field', 1, .014, silver, z=.077, side=-1, oval=(.348, .535))
ring(1, .083, polish, -1, .005, .005, oval=(.360, .548))
ring(1, .084, bright, -1, .002, .003, oval=(.332, .516))
for i in range(-7, 8):
    y = i * .054
    extent = .295 * math.sqrt(1 - (y / .49) ** 2)
    ridge('Cartouche engine turning', [(-extent, y, .085), (extent, y, .085)],
          bright, .0009, .001, -1)
for mirror in (-1, 1):
    stem = []
    for i in range(49):
        u = i / 48
        y = -.45 + .88 * u
        x = mirror * (.42 + .075 * math.sin(math.pi * u))
        stem.append((x, y, .080))
    ridge('Acanthus stem', stem, polish, .005, .006, -1)
    for i in range(9):
        u = (i + 1) / 10
        y = -.45 + .88 * u
        x = mirror * (.42 + .075 * math.sin(math.pi * u))
        leaf((x, y), (x + mirror * (.12 - .025 * abs(2 * u - 1)), y + .09), .033, .018)
    for y, direction in ((.43, 1), (-.45, -1)):
        points = []
        for i in range(42):
            u = i / 41
            t = direction * (math.pi * .35 + u * math.pi * 1.85)
            r = .09 * (1 - .82 * u)
            points.append((mirror * (.42 + r * math.cos(t)), y + r * math.sin(t), .080))
        ridge('Cartouche scroll terminal', points, polish, .004, .005, -1)
text_mesh('1', 0, .052, .70, -1, z=.094, material=polish, width=.32)
text_mesh('2026', 0, -.416, .064, -1, z=.089, material=polish)
arc_text('ZHIFOU', side=-1)
arc_text('ONE', side=-1, bottom=True)

print('MINT_GEOMETRY', sorted([(sum(len(p.vertices) - 2 for p in part.data.polygons), part.name)
                             for part in parts], reverse=True)[:8])
bpy.ops.object.select_all(action='DESELECT')
for part in parts:
    part.select_set(True)
bpy.context.view_layer.objects.active = parts[0]
bpy.ops.object.join()
coin = bpy.context.object
coin.name = 'Original astrolabe coin master'
bpy.ops.object.transform_apply(location=False, rotation=True, scale=True)
coin.location = (-1.13, 0, .14)
coin.rotation_euler.z = -.16
reverse = coin.copy()
reverse.data = coin.data
scene.collection.objects.link(reverse)
reverse.location = (1.13, .1, .14)
reverse.rotation_euler = (0, math.pi, .13)

floor = bpy.data.materials.new('Neutral studio floor')
floor.use_nodes = True
floor.node_tree.nodes.get('Principled BSDF').inputs['Base Color'].default_value = (.10, .13, .14, 1)
floor.node_tree.nodes.get('Principled BSDF').inputs['Roughness'].default_value = .8
bpy.ops.mesh.primitive_plane_add(size=200, location=(0, 0, -.005))
bpy.context.object.data.materials.append(floor)
scene.world.color = (.22, .22, .22)
for location, power, size in [((-3, -4, 7), 1100, 5), ((4, 2, 5), 850, 3), ((0, 4, 2), 650, 2)]:
    bpy.ops.object.light_add(type='AREA', location=location)
    obj = bpy.context.object
    obj.data.energy, obj.data.size = power, size
    obj.rotation_euler = (Vector((0, 0, 0)) - obj.location).to_track_quat('-Z', 'Y').to_euler()
bpy.ops.object.camera_add(location=(.2, -5.7, 7.8))
camera = bpy.context.object
camera.rotation_euler = (Vector((0, 0, 0)) - camera.location).to_track_quat('-Z', 'Y').to_euler()
camera.data.type = 'ORTHO'
camera.data.ortho_scale = 5.25
scene.camera = camera
scene.render.engine = 'CYCLES'
scene.cycles.samples = 32
scene.cycles.use_denoising = True
scene.render.resolution_x, scene.render.resolution_y = 1280, 880
scene.render.resolution_percentage = 100
scene.render.image_settings.file_format = 'PNG'
scene.render.filepath = str(out / 'coin-preview.png')
bpy.ops.render.render(write_still=True)
scene.render.filepath = '//coin-preview.png'

runtime = bpy.data.scenes.new('Runtime - ten shared meshes')
mesh = coin.data.copy()
mesh.transform(Matrix.Rotation(math.pi / 2, 4, 'X'))
for index in range(10):
    obj = bpy.data.objects.new(f'Coin_{index + 1:02d}', mesh)
    runtime.collection.objects.link(obj)
bpy.context.window.scene = runtime
bpy.ops.export_scene.gltf(filepath=str(out / 'zhifou_coin.glb'), export_format='GLB',
    use_active_scene=True, export_cameras=False, export_lights=False, export_extras=False)
bpy.context.window.scene = scene
for unused in list(bpy.data.fonts):
    if unused.users == 0 and unused.name != 'Bfont':
        bpy.data.fonts.remove(unused)
bpy.ops.wm.save_as_mainfile(filepath=str(out / 'zhifou_coin.blend'), compress=True)
mesh.calc_loop_triangles()
print(f'COIN_MODEL triangles={len(mesh.loop_triangles)} vertices={len(mesh.vertices)} instances=10 materials=5')
