"""Render the coin and dice catalog art in Blender, without changing runtime models."""
import argparse
import math
from pathlib import Path
import sys

import bpy
from mathutils import Matrix, Quaternion, Vector


PIP_PATTERNS = {
    1: ((0, 0),),
    2: ((-1, 1), (1, -1)),
    3: ((-1, 1), (0, 0), (1, -1)),
    4: ((-1, -1), (-1, 1), (1, -1), (1, 1)),
    5: ((-1, -1), (-1, 1), (0, 0), (1, -1), (1, 1)),
    6: ((-1, -1), (-1, 0), (-1, 1), (1, -1), (1, 0), (1, 1)),
}
# Number, outward normal, in-plane right, in-plane up. Opposite faces sum to seven.
DIE_FACES = (
    (1, (1, 0, 0), (0, 1, 0), (0, 0, 1)),
    (6, (-1, 0, 0), (0, -1, 0), (0, 0, 1)),
    (4, (0, 1, 0), (-1, 0, 0), (0, 0, 1)),
    (3, (0, -1, 0), (1, 0, 0), (0, 0, 1)),
    (5, (0, 0, 1), (1, 0, 0), (0, 1, 0)),
    (2, (0, 0, -1), (-1, 0, 0), (0, 1, 0)),
)


def validate_die():
    assert sorted(number for number, *_ in DIE_FACES) == list(range(1, 7))
    for number, normal, right, up in DIE_FACES:
        assert len(PIP_PATTERNS[number]) == number
        assert len(set(PIP_PATTERNS[number])) == number
        assert all((-x, -y) in PIP_PATTERNS[number] for x, y in PIP_PATTERNS[number])
        assert Vector(right).cross(Vector(up)) == Vector(normal)
        opposite = next(n for n, axis, *_ in DIE_FACES if Vector(axis) == -Vector(normal))
        assert number + opposite == 7


def material(name, color, metallic=0, roughness=.3):
    result = bpy.data.materials.new(name)
    result.use_nodes = True
    shader = result.node_tree.nodes.get('Principled BSDF')
    shader.inputs['Base Color'].default_value = (*color, 1)
    shader.inputs['Metallic'].default_value = metallic
    shader.inputs['Roughness'].default_value = roughness
    return result


def track(obj, target):
    obj.rotation_euler = (Vector(target) - obj.location).to_track_quat('-Z', 'Y').to_euler()


def studio(name, ground, camera_position, target, scale):
    scene = bpy.data.scenes.new(name)
    bpy.context.window.scene = scene
    scene.render.engine = 'CYCLES'
    scene.cycles.samples = 64
    scene.cycles.use_denoising = True
    scene.render.resolution_x = scene.render.resolution_y = 768
    scene.render.resolution_percentage = 100
    scene.render.image_settings.file_format = 'JPEG'
    scene.render.image_settings.quality = 92
    scene.view_settings.view_transform = 'AgX'
    scene.view_settings.look = 'AgX - Medium High Contrast'
    scene.view_settings.exposure = .65
    scene.world = bpy.data.worlds.new(name + ' soft studio')
    scene.world.use_nodes = True
    scene.world.node_tree.nodes.get('Background').inputs[0].default_value = (.82, .87, .90, 1)
    scene.world.node_tree.nodes.get('Background').inputs[1].default_value = .55
    bpy.ops.mesh.primitive_plane_add(size=200)
    bpy.context.object.name = 'Seamless pastel floor'
    bpy.context.object.data.materials.append(material(name + ' backdrop', ground, roughness=.82))
    for label, location, power, width, height in (
        ('Key softbox', (-3, -4, 6), 850, 4.5, 3),
        ('White rim strip', (3, 1, 4), 1050, 1.8, 4),
        ('Front fill', (0, -5, 2), 250, 3, 3),
    ):
        bpy.ops.object.light_add(type='AREA', location=location)
        light = bpy.context.object
        light.name = label
        light.data.energy = power
        light.data.shape = 'RECTANGLE'
        light.data.size = width
        light.data.size_y = height
        track(light, (0, 0, .5))
    bpy.ops.object.camera_add(location=camera_position)
    camera = bpy.context.object
    camera.data.type = 'ORTHO'
    camera.data.ortho_scale = scale
    track(camera, target)
    scene.camera = camera
    return scene


def grounded(obj, x, y, lift=.012):
    obj.location = (x, y, 0)
    rotation = obj.rotation_euler.to_matrix() if obj.rotation_mode != 'QUATERNION' else obj.rotation_quaternion.to_matrix()
    bottom = min((rotation @ vertex.co).z for vertex in obj.data.vertices)
    obj.location.z = -bottom + lift


def coin_card(source):
    scene = studio('Coin card', (.88, .78, .75), (2.4, -7, 6.3), (0, 0, .48), 4.95)
    with bpy.data.libraries.load(str(source), link=False) as (available, loaded):
        name = 'Original astrolabe coin master'
        assert name in available.objects, 'Rebuild the original coin source first'
        loaded.objects = [name]
    pattern = loaded.objects[0]
    scene.collection.objects.link(pattern)
    pattern.name = 'Card - exact runtime astrolabe face'
    pattern.rotation_euler = (math.radians(57), 0, math.radians(-16))
    grounded(pattern, -.66, .24)
    number = pattern.copy()
    number.data = pattern.data
    scene.collection.objects.link(number)
    number.name = 'Card - exact runtime numeral face'
    number.rotation_euler = (0, math.pi, math.radians(18))
    grounded(number, .70, -.43)
    return scene


def die(name, color, pip_color):
    bpy.ops.mesh.primitive_cube_add(size=2)
    obj = bpy.context.object
    obj.name = name
    obj.data.materials.append(material(name + ' enamel', color, metallic=.08, roughness=.22))
    obj.data.materials.append(material(name + ' inset pips', pip_color, roughness=.32))
    bevel = obj.modifiers.new('Rounded real die corners', 'BEVEL')
    bevel.width, bevel.segments = .16, 6
    bpy.context.view_layer.objects.active = obj
    bpy.ops.object.modifier_apply(modifier=bevel.name)
    cutters = []
    for number, normal, right, up in DIE_FACES:
        for x, y in PIP_PATTERNS[number]:
            center = Vector(normal) * 1.075 + Vector(right) * (x * .43) + Vector(up) * (y * .43)
            bpy.ops.mesh.primitive_uv_sphere_add(segments=24, ring_count=12, radius=.155, location=center)
            cutter = bpy.context.object
            cutter.data.materials.append(obj.data.materials[0])
            cutter.data.materials.append(obj.data.materials[1])
            for polygon in cutter.data.polygons:
                polygon.material_index = 1
                polygon.use_smooth = True
            cutters.append(cutter)
    bpy.ops.object.select_all(action='DESELECT')
    for cutter in cutters:
        cutter.select_set(True)
    bpy.context.view_layer.objects.active = cutters[0]
    bpy.ops.object.join()
    cutter = bpy.context.object
    modifier = obj.modifiers.new('21 recessed pips - six correct faces', 'BOOLEAN')
    modifier.operation, modifier.solver, modifier.object = 'DIFFERENCE', 'EXACT', cutter
    bpy.context.view_layer.objects.active = obj
    bpy.ops.object.modifier_apply(modifier=modifier.name)
    bpy.data.objects.remove(cutter, do_unlink=True)
    for polygon in obj.data.polygons:
        polygon.use_smooth = True
    normals = obj.modifiers.new('Keep broad faces flat', 'WEIGHTED_NORMAL')
    normals.keep_sharp = True
    return obj


def dice_card():
    scene = studio('Dice card', (.75, .86, .80), (4, -8, 6.5), (0, 0, .7), 6.65)
    scene.view_settings.exposure = 0
    scene.world.node_tree.nodes.get('Background').inputs[1].default_value = .35
    for obj in scene.objects:
        if obj.type == 'LIGHT':
            obj.data.energy *= .5
    first = die('Ivory die', (.91, .94, .91), (.025, .12, .075))
    first.rotation_euler.z = math.radians(-13)
    grounded(first, -1.26, -.08)
    second = die('Sage die', (.31, .48, .37), (.92, .95, .90))
    # A rigid rotation, not different pip assignments: top 6, front 2, right 3.
    basis = Matrix(((0, -1, 0), (0, 0, 1), (-1, 0, 0)))
    assert abs(basis.determinant() - 1) < .0001
    second.rotation_mode = 'QUATERNION'
    second.rotation_quaternion = Quaternion((0, 0, 1), math.radians(9)) @ basis.to_quaternion()
    grounded(second, 1.26, .15)
    return scene


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    root = Path(__file__).resolve().parents[1]
    parser.add_argument('--coin-source', type=Path, default=root / 'art/coin/zhifou_coin.blend')
    parser.add_argument('--out', type=Path, required=True)
    parser.add_argument('--asset', choices=('both', 'coin', 'dice'), default='both')
    args = parser.parse_args(sys.argv[sys.argv.index('--') + 1:])
    validate_die()
    bpy.ops.wm.read_factory_settings(use_empty=True)
    args.out.mkdir(parents=True, exist_ok=True)
    for kind in ('coin', 'dice'):
        if args.asset not in ('both', kind):
            continue
        scene = coin_card(args.coin_source.resolve()) if kind == 'coin' else dice_card()
        scene.render.filepath = str((args.out / f'tool_{kind}_refined.jpg').resolve())
        bpy.ops.render.render(write_still=True)
        print(f'TOOL_CARD {kind}: 768x768; deterministic geometry; {scene.render.filepath}')


if __name__ == '__main__':
    main()
