package dev.mcmc.mod.client;

import org.lwjgl.opengl.GLCapabilities;

import java.util.function.Function;

/**
 * @author LatvianModder
 */
public enum ARBCaps
{
	ARRAYS_OF_ARRAYS(c -> c.GL_ARB_arrays_of_arrays),
	BASE_INSTANCE(c -> c.GL_ARB_base_instance),
	BLEND_FUNC_EXTENDED(c -> c.GL_ARB_blend_func_extended),
	CLEAR_BUFFER_OBJECT(c -> c.GL_ARB_clear_buffer_object),
	COLOR_BUFFER_FLOAT(c -> c.GL_ARB_color_buffer_float),
	COMPATIBILITY(c -> c.GL_ARB_compatibility),
	COMPRESSED_TEXTURE_PIXEL_STORAGE(c -> c.GL_ARB_compressed_texture_pixel_storage),
	COMPUTE_SHADER(c -> c.GL_ARB_compute_shader),
	COPY_BUFFER(c -> c.GL_ARB_copy_buffer),
	COPY_IMAGE(c -> c.GL_ARB_copy_image),
	DEPTH_BUFFER_FLOAT(c -> c.GL_ARB_depth_buffer_float),
	DEPTH_CLAMP(c -> c.GL_ARB_depth_clamp),
	DEPTH_TEXTURE(c -> c.GL_ARB_depth_texture),
	DRAW_BUFFERS(c -> c.GL_ARB_draw_buffers),
	DRAW_BUFFERS_BLEND(c -> c.GL_ARB_draw_buffers_blend),
	DRAW_ELEMENTS_BASE_VERTEX(c -> c.GL_ARB_draw_elements_base_vertex),
	DRAW_INDIRECT(c -> c.GL_ARB_draw_indirect),
	DRAW_INSTANCED(c -> c.GL_ARB_draw_instanced),
	EXPLICIT_ATTRIB_LOCATION(c -> c.GL_ARB_explicit_attrib_location),
	EXPLICIT_UNIFORM_LOCATION(c -> c.GL_ARB_explicit_uniform_location),
	FRAGMENT_LAYER_VIEWPORT(c -> c.GL_ARB_fragment_layer_viewport),
	FRAGMENT_PROGRAM(c -> c.GL_ARB_fragment_program),
	FRAGMENT_SHADER(c -> c.GL_ARB_fragment_shader),
	FRAGMENT_PROGRAM_SHADOW(c -> c.GL_ARB_fragment_program_shadow),
	FRAMEBUFFER_OBJECT(c -> c.GL_ARB_framebuffer_object),
	FRAMEBUFFER_SRGB(c -> c.GL_ARB_framebuffer_sRGB),
	GEOMETRY_SHADER4(c -> c.GL_ARB_geometry_shader4),
	GPU_SHADER5(c -> c.GL_ARB_gpu_shader5),
	HALF_FLOAT_PIXEL(c -> c.GL_ARB_half_float_pixel),
	HALF_FLOAT_VERTEX(c -> c.GL_ARB_half_float_vertex),
	INSTANCED_ARRAYS(c -> c.GL_ARB_instanced_arrays),
	MAP_BUFFER_ALIGNMENT(c -> c.GL_ARB_map_buffer_alignment),
	MAP_BUFFER_RANGE(c -> c.GL_ARB_map_buffer_range),
	MULTISAMPLE(c -> c.GL_ARB_multisample),
	MULTITEXTURE(c -> c.GL_ARB_multitexture),
	OCCLUSION_QUERY2(c -> c.GL_ARB_occlusion_query2),
	PIXEL_BUFFER_OBJECT(c -> c.GL_ARB_pixel_buffer_object),
	SEAMLESS_CUBE_MAP(c -> c.GL_ARB_seamless_cube_map),
	SHADER_OBJECTS(c -> c.GL_ARB_shader_objects),
	SHADER_STENCIL_EXPORT(c -> c.GL_ARB_shader_stencil_export),
	SHADER_TEXTURE_LOD(c -> c.GL_ARB_shader_texture_lod),
	SHADOW(c -> c.GL_ARB_shadow),
	SHADOW_AMBIENT(c -> c.GL_ARB_shadow_ambient),
	STENCIL_TEXTURING(c -> c.GL_ARB_stencil_texturing),
	SYNC(c -> c.GL_ARB_sync),
	TESSELLATION_SHADER(c -> c.GL_ARB_tessellation_shader),
	TEXTURE_BORDER_CLAMP(c -> c.GL_ARB_texture_border_clamp),
	TEXTURE_BUFFER_OBJECT(c -> c.GL_ARB_texture_buffer_object),
	TEXTURE_CUBE_MAP(c -> c.GL_ARB_texture_cube_map),
	TEXTURE_CUBE_MAP_ARRAY(c -> c.GL_ARB_texture_cube_map_array),
	TEXTURE_NON_POWER_OF_TWO(c -> c.GL_ARB_texture_non_power_of_two),
	UNIFORM_BUFFER_OBJECT(c -> c.GL_ARB_uniform_buffer_object),
	VERTEX_BLEND(c -> c.GL_ARB_vertex_blend),
	VERTEX_BUFFER_OBJECT(c -> c.GL_ARB_vertex_buffer_object),
	VERTEX_PROGRAM(c -> c.GL_ARB_vertex_program),
	VERTEX_SHADER(c -> c.GL_ARB_vertex_shader),

	;

	public final Function<GLCapabilities, Boolean> getter;

	ARBCaps(Function<GLCapabilities, Boolean> g)
	{
		getter = g;
	}

	public static long get(GLCapabilities c)
	{
		long bits = 0L;

		for (ARBCaps cap : values())
		{
			if (cap.getter.apply(c))
			{
				bits |= 1L << (long) cap.ordinal();
			}
		}

		return bits;
	}
}