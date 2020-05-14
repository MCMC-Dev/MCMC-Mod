package dev.mcmc.mod.client;

import org.lwjgl.opengl.GLCapabilities;

import java.util.function.Function;

/**
 * @author LatvianModder
 */
public enum EXTCaps
{
	BINDABLE_UNIFORM(c -> c.GL_EXT_bindable_uniform),
	BLEND_EQUATION_SEPARATE(c -> c.GL_EXT_blend_equation_separate),
	BLEND_FUNC_SEPARATE(c -> c.GL_EXT_blend_func_separate),
	BLEND_MINMAX(c -> c.GL_EXT_blend_minmax),
	BLEND_SUBTRACT(c -> c.GL_EXT_blend_subtract),
	DRAW_INSTANCED(c -> c.GL_EXT_draw_instanced),
	FRAMEBUFFER_MULTISAMPLE(c -> c.GL_EXT_framebuffer_multisample),
	FRAMEBUFFER_OBJECT(c -> c.GL_EXT_framebuffer_object),
	FRAMEBUFFER_SRGB(c -> c.GL_EXT_framebuffer_sRGB),
	GEOMETRY_SHADER4(c -> c.GL_EXT_geometry_shader4),
	GPU_PROGRAM_PARAMETERS(c -> c.GL_EXT_gpu_program_parameters),
	GPU_SHADER4(c -> c.GL_EXT_gpu_shader4),
	PACKED_DEPTH_STENCIL(c -> c.GL_EXT_packed_depth_stencil),
	SEPARATE_SHADER_OBJECTS(c -> c.GL_EXT_separate_shader_objects),
	SHADER_IMAGE_LOAD_STORE(c -> c.GL_EXT_shader_image_load_store),
	SHADOW_FUNCS(c -> c.GL_EXT_shadow_funcs),
	SHARED_TEXTURE_PALETTE(c -> c.GL_EXT_shared_texture_palette),
	STENCIL_CLEAR_TAG(c -> c.GL_EXT_stencil_clear_tag),
	STENCIL_TWO_SIDE(c -> c.GL_EXT_stencil_two_side),
	STENCIL_WRAP(c -> c.GL_EXT_stencil_wrap),
	TEXTURE_ARRAY(c -> c.GL_EXT_texture_array),
	TEXTURE_BUFFER_OBJECT(c -> c.GL_EXT_texture_buffer_object),
	TEXTURE_INTEGER(c -> c.GL_EXT_texture_integer),
	TEXTURE_SRGB(c -> c.GL_EXT_texture_sRGB),

	;

	public final Function<GLCapabilities, Boolean> getter;

	EXTCaps(Function<GLCapabilities, Boolean> g)
	{
		getter = g;
	}

	public static long get(GLCapabilities c)
	{
		long bits = 0L;

		for (EXTCaps cap : values())
		{
			if (cap.getter.apply(c))
			{
				bits |= 1L << (long) cap.ordinal();
			}
		}

		return bits;
	}
}